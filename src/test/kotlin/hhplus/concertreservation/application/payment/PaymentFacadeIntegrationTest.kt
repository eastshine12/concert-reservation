import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.payment.PaymentFacade
import hhplus.concertreservation.application.payment.dto.command.PaymentCommand
import hhplus.concertreservation.application.payment.dto.info.PaymentInfo
import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest(classes = [ConcertReservationApplication::class])
class PaymentFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var paymentFacade: PaymentFacade
    private lateinit var user: User
    private lateinit var schedule: ConcertSchedule
    private lateinit var reservation: Reservation
    private lateinit var seat: Seat
    private lateinit var waitingQueue: WaitingQueue

    @BeforeEach
    fun setUp() {
        user = userJpaRepository.save(User(name = "User1", email = "user1@test.com", balance = BigDecimal(100_000)))
        schedule = concertScheduleJpaRepository.save(
            ConcertSchedule(
                concertId = 1L,
                startTime = LocalDateTime.now(),
                totalSeats = 5,
                availableSeats = 5
            )
        )
        seat = seatJpaRepository.save(
            Seat(
                scheduleId = schedule.id,
                seatNumber = 1,
                price = BigDecimal(70_000),
                status = SeatStatus.UNAVAILABLE
            )
        )
        reservation = reservationJpaRepository.save(
            Reservation(
                userId = user.id,
                scheduleId = schedule.id,
                seatId = seat.id,
                status = ReservationStatus.PENDING,
                expiresAt = LocalDateTime.now().plusMinutes(10)
            )
        )
        waitingQueue = waitingQueueJpaRepository.save(
            WaitingQueue(
                scheduleId = schedule.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
                status = QueueStatus.ACTIVE,
                queuePosition = 1,
                expiresAt = LocalDateTime.now().plusMinutes(10)
            )
        )
    }

    @Test
    fun `must process payment successfully`() {
        // given
        val command = PaymentCommand(
            userId = user.id,
            reservationId = reservation.id,
            token = "123e4567-e89b-12d3-a456-426614174000"
        )

        // when
        val paymentInfo: PaymentInfo = paymentFacade.processPayment(command)

        // then
        assertNotNull(paymentInfo)
        assertEquals(BigDecimal("70000.00"), paymentInfo.amount)
        assertEquals("SUCCESS", paymentInfo.status)

        assertEquals(ReservationStatus.CONFIRMED, reservationJpaRepository.findById(reservation.id).get().status)
        assertEquals(SeatStatus.UNAVAILABLE, seatJpaRepository.findById(seat.id).get().status)
        assertEquals(BigDecimal("30000.00"), userJpaRepository.findById(user.id).get().balance)

        val payments = paymentJpaRepository.findAllByUserId(user.id)
        assertEquals(1, payments.size)
        assertEquals(BigDecimal("70000.00"), payments[0].amount)
        assertEquals(PaymentStatus.SUCCESS, payments[0].status)

        val expiredQueue = waitingQueueJpaRepository.findById(waitingQueue.id).get()
        assertEquals(QueueStatus.EXPIRED, expiredQueue.status)
    }
}
