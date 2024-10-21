import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.payment.PaymentFacade
import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.InvalidReservationStatusException
import hhplus.concertreservation.domain.concert.exception.ReservationNotFoundException
import hhplus.concertreservation.domain.concert.exception.SeatAvailabilityException
import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.exception.InsufficientBalanceException
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.exception.TokenExpiredException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        schedule =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = 1L,
                    startTime = LocalDateTime.now(),
                    totalSeats = 5,
                    availableSeats = 5,
                ),
            )
        seat =
            seatJpaRepository.save(
                Seat(
                    scheduleId = schedule.id,
                    seatNumber = 1,
                    price = BigDecimal(70_000),
                    status = SeatStatus.UNAVAILABLE,
                ),
            )
        reservation =
            reservationJpaRepository.save(
                Reservation(
                    userId = user.id,
                    scheduleId = schedule.id,
                    seatId = seat.id,
                    status = ReservationStatus.PENDING,
                    expiresAt = LocalDateTime.now().plusMinutes(10),
                ),
            )
        waitingQueue =
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule.id,
                    token = "123e4567-e89b-12d3-a456-426614174000",
                    status = QueueStatus.ACTIVE,
                    queuePosition = 1,
                    expiresAt = LocalDateTime.now().plusMinutes(10),
                ),
            )
    }

    @Test
    fun `must process payment successfully`() {
        // given
        val command =
            PaymentCommand(
                userId = user.id,
                reservationId = reservation.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
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

    @Test
    fun `should throw exception when user has insufficient balance for payment`() {
        // given
        val reservationId = 1L
        val user =
            userJpaRepository.save(
                User(
                    name = "Test",
                    email = "test@test.com",
                    // 잔액 부족
                    balance = BigDecimal("5000.00"),
                ),
            )

        val command =
            PaymentCommand(
                userId = user.id,
                reservationId = reservationId,
                token = "123e4567-e89b-12d3-a456-426614174000",
            )

        // when & then
        val exception =
            assertThrows<InsufficientBalanceException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Insufficient balance", exception.message)
    }

    @Test
    fun `should throw exception when reservation id is invalid`() {
        // given
        val userId = user.id
        val invalidReservationId = 999L // 존재하지 않는 예약 ID
        val command =
            PaymentCommand(
                userId = userId,
                reservationId = invalidReservationId,
                token = "123e4567-e89b-12d3-a456-426614174000",
            )

        // when & then
        val exception =
            assertThrows<ReservationNotFoundException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Reservation not found with id $invalidReservationId", exception.message)
    }

    @Test
    fun `should throw exception when reservation is not in pending state during confirmation`() {
        // given
        val userId = user.id
        val reservation =
            reservationJpaRepository.save(
                Reservation(
                    userId = userId,
                    scheduleId = schedule.id,
                    seatId = seat.id,
                    // 예약 확정 상태
                    status = ReservationStatus.CONFIRMED,
                    expiresAt = LocalDateTime.now().plusMinutes(10),
                ),
            )
        val command =
            PaymentCommand(
                userId = userId,
                reservationId = reservation.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
            )

        // when & then
        val exception =
            assertThrows<InvalidReservationStatusException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Reservation is not in pending state", exception.message)
    }

    @Test
    fun `should throw exception when token is expired during payment process`() {
        // given
        val userId = user.id
        val reservationId = reservation.id
        val expiredWaitingQueue =
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule.id,
                    token = "123e4567-e89b-12d3-a456-426614174001",
                    status = QueueStatus.EXPIRED,
                    queuePosition = 1,
                    expiresAt = LocalDateTime.now().minusMinutes(10),
                ),
            )
        val command =
            PaymentCommand(
                userId = userId,
                reservationId = reservationId,
                token = expiredWaitingQueue.token,
            )

        // when & then
        val exception =
            assertThrows<TokenExpiredException> {
                paymentFacade.processPayment(command)
            }
        assertEquals("Token has expired: ${command.token}", exception.message)
    }

    @Test
    fun `should throw exception when seat is no longer valid during payment process`() {
        // given
        val userId = user.id
        val schedule =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = 1L,
                    startTime = LocalDateTime.now(),
                    totalSeats = 5,
                    availableSeats = 4,
                ),
            )
        val seat =
            seatJpaRepository.save(
                Seat(
                    scheduleId = schedule.id,
                    seatNumber = 1,
                    price = BigDecimal(70_000),
                    status = SeatStatus.AVAILABLE,
                ),
            )
        val reservation =
            reservationJpaRepository.save(
                Reservation(
                    userId = user.id,
                    scheduleId = schedule.id,
                    seatId = seat.id,
                    status = ReservationStatus.PENDING,
                    expiresAt = LocalDateTime.now().plusMinutes(10),
                ),
            )
        val command =
            PaymentCommand(
                userId = userId,
                reservationId = reservation.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
            )

        // when & then
        val exception =
            assertThrows<SeatAvailabilityException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("The seat is not reserved with id ${seat.id}", exception.message)
    }

    @Test
    fun `should throw exception when trying to pay for an already confirmed reservation on second attempt`() {
        // given
        val userId = user.id
        val reservationId = reservation.id
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val paymentCommand =
            PaymentCommand(
                userId = userId,
                reservationId = reservationId,
                token = token,
            )

        // 첫 번째 결제
        paymentFacade.processPayment(paymentCommand)

        // when & then - 두 번째 결제
        val exception =
            assertThrows<TokenExpiredException> {
                paymentFacade.processPayment(paymentCommand)
            }

        assertEquals("Token has expired: $token", exception.message)
    }
}
