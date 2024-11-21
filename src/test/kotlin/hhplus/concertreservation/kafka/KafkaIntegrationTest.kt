package hhplus.concertreservation.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.test.assertTrue

@SpringBootTest
class KafkaIntegrationTest {
    private lateinit var kafkaContainer: KafkaContainer
    private lateinit var producerProps: Properties
    private lateinit var consumerProps: Properties

    @BeforeEach
    fun setUp() {
        kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
        kafkaContainer.start()

        // Kafka Producer 설정
        producerProps =
            Properties().apply {
                put("bootstrap.servers", kafkaContainer.bootstrapServers)
                put("key.serializer", StringSerializer::class.java.canonicalName)
                put("value.serializer", StringSerializer::class.java.canonicalName)
            }

        // Kafka Consumer 설정
        consumerProps =
            Properties().apply {
                put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
                put(ConsumerConfig.GROUP_ID_CONFIG, "test-group")
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.canonicalName)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.canonicalName)
                put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            }
    }

    @AfterEach
    fun tearDown() {
        kafkaContainer.stop()
    }

    @Test
    fun `test Kafka producer and consumer with Testcontainers`() {
        // given
        val producer = KafkaProducer<String, String>(producerProps)
        producer.send(ProducerRecord("test-topic", "Hello, Kafka!"))
        producer.close()

        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf("test-topic"))

        // when
        val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
        consumer.close()

        // then
        assertTrue(records.count() > 0, "No messages consumed")
        assertEquals("Hello, Kafka!", records.iterator().next().value())
    }

    @Test
    fun `must consume 100 messages produced concurrently`() {
        // given
        val producer = KafkaProducer<String, String>(producerProps)
        val messages = (1..100).map { "Message$it" }
        val executor = Executors.newFixedThreadPool(10)

        val tasks =
            messages.map { message ->
                Callable {
                    producer.send(ProducerRecord("test-topic2", message))
                }
            }

        executor.invokeAll(tasks)
        executor.shutdown()
        producer.close()

        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf("test-topic2"))

        // when
        val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(10))
        consumer.close()

        // then
        val consumedMessages = records.map { it.value() }.toSet()
        assertEquals(100, consumedMessages.size)
        assertTrue(messages.all { it in consumedMessages })
    }

    @Test
    fun `must ensure messages with the same key are in the same partition`() {
        // given
        val topic = "test-topic-three-partitions"
        val messages =
            listOf(
                "Key1" to "Message1",
                "Key2" to "Message2",
                "Key3" to "Message3",
                "Key1" to "Message4",
                "Key2" to "Message5",
                "Key3" to "Message6",
            )

        // create topic
        val adminProps =
            Properties().apply {
                put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            }
        val adminClient = AdminClient.create(adminProps)
        val newTopic = NewTopic(topic, 3, 1.toShort())
        adminClient.createTopics(listOf(newTopic)).all().get()
        adminClient.close()

        // produce
        val producer = KafkaProducer<String, String>(producerProps)
        messages.forEach { (key, message) ->
            producer.send(ProducerRecord(topic, key, message))
        }
        producer.close()

        // consume
        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.assign(
            listOf(
                TopicPartition(topic, 0),
                TopicPartition(topic, 1),
                TopicPartition(topic, 2),
            )
        )
        val partitionMessages = mutableMapOf<Int, MutableList<Pair<String, String>>>()
        consumer.seekToBeginning(consumer.assignment())
        val records = consumer.poll(Duration.ofSeconds(10))
        records.forEach { record ->
            partitionMessages.computeIfAbsent(record.partition()) { mutableListOf() }
                .add(record.key() to record.value())
        }
        consumer.close()

        // then
        partitionMessages.forEach { (partition, messages) ->
            println("Partition $partition: $messages")
        }

        messages.forEach { (key, message) ->
            val partition = partitionMessages.entries.find { it.value.contains(key to message) }?.key
            assertNotNull(partition)
        }

        val partitionNumForMessage1 = partitionMessages.entries.find { it.value.contains("Key1" to "Message1") }?.key
        val partitionNumForMessage2 = partitionMessages.entries.find { it.value.contains("Key2" to "Message2") }?.key
        val partitionNumForMessage3 = partitionMessages.entries.find { it.value.contains("Key3" to "Message3") }?.key
        val partitionNumForMessage4 = partitionMessages.entries.find { it.value.contains("Key1" to "Message4") }?.key
        val partitionNumForMessage5 = partitionMessages.entries.find { it.value.contains("Key2" to "Message5") }?.key
        val partitionNumForMessage6 = partitionMessages.entries.find { it.value.contains("Key3" to "Message6") }?.key
        assertEquals(partitionNumForMessage1, partitionNumForMessage4)
        assertEquals(partitionNumForMessage2, partitionNumForMessage5)
        assertEquals(partitionNumForMessage3, partitionNumForMessage6)
    }
}
