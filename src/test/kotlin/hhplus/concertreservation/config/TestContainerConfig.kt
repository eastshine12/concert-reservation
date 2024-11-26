package hhplus.concertreservation.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestContainerConfig {
    @Bean
    fun kafkaContainer(environment: Environment): KafkaContainer {
        val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
        container.start()

        if (environment is StandardEnvironment) {
            val propertySources: MutablePropertySources = environment.propertySources
            propertySources.addFirst(
                org.springframework.core.env.MapPropertySource(
                    "kafkaProperties",
                    mapOf("spring.kafka.bootstrap-servers" to container.bootstrapServers),
                ),
            )
        }
        return container
    }

    @Bean
    fun redisContainer(environment: Environment): GenericContainer<*> {
        val container =
            GenericContainer<Nothing>("redis:latest").apply {
                withExposedPorts(6379)
            }
        container.start()

        if (environment is StandardEnvironment) {
            val propertySources: MutablePropertySources = environment.propertySources
            propertySources.addFirst(
                org.springframework.core.env.MapPropertySource(
                    "redisProperties",
                    mapOf(
                        "spring.data.redis.host" to container.host,
                        "spring.data.redis.port" to container.getMappedPort(6379).toString(),
                    ),
                ),
            )
        }
        return container
    }
}
