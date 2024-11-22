package hhplus.concertreservation

import hhplus.concertreservation.infrastructure.repository.concert.ConcertJpaRepository
import hhplus.concertreservation.infrastructure.repository.concert.ConcertScheduleJpaRepository
import hhplus.concertreservation.infrastructure.repository.concert.ReservationJpaRepository
import hhplus.concertreservation.infrastructure.repository.concert.SeatJpaRepository
import hhplus.concertreservation.infrastructure.repository.outbox.OutboxJpaRepository
import hhplus.concertreservation.infrastructure.repository.payment.PaymentJpaRepository
import hhplus.concertreservation.infrastructure.repository.user.BalanceHistoryJpaRepository
import hhplus.concertreservation.infrastructure.repository.user.UserJpaRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaTemplate

@Import(hhplus.concertreservation.config.TestContainerConfig::class)
abstract class IntegrationTestBase {
    @Autowired
    protected lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    protected lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var kafkaListenerContainerFactory: ConcurrentKafkaListenerContainerFactory<String, String>

    @Autowired
    protected lateinit var concertJpaRepository: ConcertJpaRepository

    @Autowired
    protected lateinit var concertScheduleJpaRepository: ConcertScheduleJpaRepository

    @Autowired
    protected lateinit var seatJpaRepository: SeatJpaRepository

    @Autowired
    protected lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    protected lateinit var reservationJpaRepository: ReservationJpaRepository

    @Autowired
    protected lateinit var paymentJpaRepository: PaymentJpaRepository

    @Autowired
    protected lateinit var balanceHistoryJpaRepository: BalanceHistoryJpaRepository

    @Autowired
    protected lateinit var outboxJpaRepository: OutboxJpaRepository

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun tearDown() {
        concertJpaRepository.deleteAll()
        concertScheduleJpaRepository.deleteAll()
        seatJpaRepository.deleteAll()
        userJpaRepository.deleteAll()
        reservationJpaRepository.deleteAll()
        paymentJpaRepository.deleteAll()
        balanceHistoryJpaRepository.deleteAll()
        outboxJpaRepository.deleteAll()
        resetAutoIncrement("concert_schedule")
        resetAutoIncrement("concert")
        resetAutoIncrement("users")
        resetAutoIncrement("seat")
        resetAutoIncrement("reservation")
        resetAutoIncrement("payment")
        resetAutoIncrement("balance_history")
        resetAutoIncrement("outbox")
    }

    protected fun resetAutoIncrement(tableName: String) {
        jdbcTemplate.execute("ALTER TABLE \"$tableName\" ALTER COLUMN \"id\" RESTART WITH 1")
    }
}
