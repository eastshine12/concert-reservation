package hhplus.concertreservation.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.*
import kotlin.test.assertTrue

@SpringBootTest
class KafkaIntegrationTest {
    private lateinit var kafkaContainer: KafkaContainer

    @BeforeEach
    fun setUp() {
        kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
        kafkaContainer.start()
    }

    @AfterEach
    fun tearDown() {
        kafkaContainer.stop()
    }

    @Test
    fun `test Kafka producer and consumer with Testcontainers`() {
        // given

        // Kafka Producer 설정
        val producerProps =
            Properties().apply {
                put("bootstrap.servers", kafkaContainer.bootstrapServers)
                put("key.serializer", StringSerializer::class.java.canonicalName)
                put("value.serializer", StringSerializer::class.java.canonicalName)
            }
        val producer = KafkaProducer<String, String>(producerProps)
        producer.send(ProducerRecord("test-topic", "Hello, Kafka!"))
        producer.close()

        // Kafka Consumer 설정
        val consumerProps =
            Properties().apply {
                put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
                put(ConsumerConfig.GROUP_ID_CONFIG, "test-group")
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.canonicalName)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.canonicalName)
                put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            }
        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf("test-topic"))

        // when
        val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))
        consumer.close()

        // then
        assertTrue(records.count() > 0, "No messages consumed")
        assertEquals("Hello, Kafka!", records.iterator().next().value())
    }
}
