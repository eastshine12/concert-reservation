import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.payment.PaymentFacade
import hhplus.concertreservation.application.user.UserFacade
import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import hhplus.concertreservation.domain.user.dto.command.ChargeBalanceCommand
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(classes = [ConcertReservationApplication::class])
class PaymentFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var paymentFacade: PaymentFacade

    @Autowired
    private lateinit var userFacade: UserFacade
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
            assertThrows<CoreException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Payment failed.", exception.message)
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
            assertThrows<CoreException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Payment failed.", exception.message)
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
            assertThrows<CoreException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Payment failed.", exception.message)
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
            assertThrows<CoreException> {
                paymentFacade.processPayment(command)
            }

        assertEquals("Payment failed.", exception.message)
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
            assertThrows<CoreException> {
                paymentFacade.processPayment(paymentCommand)
            }

        assertEquals("Payment failed.", exception.message)
    }

    @Test
    fun `should process only one successful payment when multiple requests are made concurrently`() {
        // Given
        val userId = user.id
        val reservationId = reservation.id
        val token = "123e4567-e89b-12d3-a456-426614174000"
        val paymentCommand =
            PaymentCommand(
                userId = userId,
                reservationId = reservationId,
                token = token,
            )

        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)
        val executor: ExecutorService = Executors.newFixedThreadPool(5)

        val tasks =
            (1..5).map {
                Callable {
                    try {
                        paymentFacade.processPayment(paymentCommand)
                        successCount.incrementAndGet()
                    } catch (e: CoreException) {
                        failureCount.incrementAndGet()
                    }
                }
            }

        // When
        executor.invokeAll(tasks)
        executor.shutdown()

        // Then
        assertEquals(1, successCount.get())
        assertEquals(4, failureCount.get())
    }

    @Test
    fun `should maintain correct balance when charge and payment are made concurrently`() {
        // Given
        val userId = user.id
        val reservationId = reservation.id
        val amount = BigDecimal("1000.00")
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val chargeSuccessCount = AtomicInteger(0)
        val chargeFailureCount = AtomicInteger(0)
        val paymentSuccessCount = AtomicInteger(0)
        val paymentFailureCount = AtomicInteger(0)

        val executor: ExecutorService = Executors.newFixedThreadPool(2)

        // When
        val chargeTasks =
            (1..2).map {
                Callable {
                    try {
                        userFacade.chargeBalance(
                            ChargeBalanceCommand(
                                userId = userId,
                                token = token,
                                amount = amount,
                            ),
                        )
                        chargeSuccessCount.incrementAndGet()
                    } catch (e: CoreException) {
                        chargeFailureCount.incrementAndGet()
                    }
                }
            }

        val paymentTasks =
            (1..2).map {
                Callable {
                    try {
                        paymentFacade.processPayment(
                            PaymentCommand(
                                userId = userId,
                                reservationId = reservationId,
                                token = token,
                            ),
                        )
                        paymentSuccessCount.incrementAndGet()
                    } catch (e: CoreException) {
                        paymentFailureCount.incrementAndGet()
                    }
                }
            }

        executor.invokeAll(chargeTasks + paymentTasks)
        executor.shutdown()

        // Then
        assertEquals(1, paymentSuccessCount.get())

        val chargeTotal = amount.toInt() * chargeSuccessCount.get()
        val deduct = seat.price.toInt() * paymentSuccessCount.get()
        assertEquals(user.balance.toInt() + chargeTotal - deduct, userJpaRepository.findById(userId).get().balance.toInt())
    }
}
