import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.user.UserFacade
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.user.dto.command.ChargeBalanceCommand
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.exception.InvalidBalanceAmountException
import hhplus.concertreservation.domain.user.exception.UserNotFoundException
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.junit.jupiter.api.Assertions.assertTrue
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [ConcertReservationApplication::class])
class UserFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var userFacade: UserFacade
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user =
            userJpaRepository.save(
                User(
                    name = "user1",
                    email = "user1@test.com",
                    balance = BigDecimal(1000),
                ),
            )

        waitingQueueJpaRepository.save(
            WaitingQueue(
                scheduleId = 1L,
                token = "123e4567-e89b-12d3-a456-426614174000",
                status = QueueStatus.ACTIVE,
                queuePosition = 1,
                expiresAt = LocalDateTime.now().plusMinutes(10),
            ),
        )
    }

    @Test
    fun `must handle concurrent balance charges with optimistic locking`() {
        // Given
        val userId = 1L
        val token = "123e4567-e89b-12d3-a456-426614174000"
        val command = ChargeBalanceCommand(token, userId, BigDecimal(500))
        val successCount = AtomicInteger(0)

        val executor: ExecutorService = Executors.newFixedThreadPool(5)

        // When
        val tasks =
            List(5) {
                Callable {
                    try {
                        userFacade.chargeBalance(command)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                    }
                }
            }

        executor.invokeAll(tasks)
        executor.shutdown()

        // Then
        val updatedUser = userJpaRepository.findById(userId).get()

        val successfulCharges = successCount.get()
        assertTrue(successfulCharges in 1..5)
        val expectedBalance = BigDecimal("1000.00") + (BigDecimal("500.00") * BigDecimal(successfulCharges))
        assertEquals(expectedBalance, updatedUser.balance)
    }

    @Test
    fun `must charge balance successfully`() {
        // Given
        val command =
            ChargeBalanceCommand(
                userId = user.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
                amount = BigDecimal(500),
            )

        // When
        val result: UpdateBalanceInfo = userFacade.chargeBalance(command)

        // Then
        assertNotNull(result)
        assertEquals(1L, result.balanceHistoryId)
        assertEquals(BigDecimal("1500.00"), userJpaRepository.findById(user.id).get().balance)
    }

    @Test
    fun `must get user balance`() {
        // When
        val balance: BigDecimal = userFacade.getUserBalance("123e4567-e89b-12d3-a456-426614174000", user.id)

        // Then
        assertNotNull(balance)
        assertEquals(BigDecimal("1000.00"), balance)
    }

    @Test
    fun `should throw exception when charge amount is zero or negative`() {
        // given
        val userId = user.id
        val invalidAmount = BigDecimal("0.00")
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val command =
            ChargeBalanceCommand(
                userId = userId,
                amount = invalidAmount,
                token = token,
            )

        // when & then
        val exception =
            assertThrows<InvalidBalanceAmountException> {
                userFacade.chargeBalance(command)
            }

        assertEquals("Charge amount must be positive", exception.message)
    }

    @Test
    fun `should throw exception when user id is invalid during balance charge`() {
        // given
        val invalidUserId = 999L // 존재하지 않는 사용자 ID
        val amount = BigDecimal("500.00")
        val token = "123e4567-e89b-12d3-a456-426614174000"

        val command =
            ChargeBalanceCommand(
                userId = invalidUserId,
                amount = amount,
                token = token,
            )

        // when & then
        val exception =
            assertThrows<UserNotFoundException> {
                userFacade.chargeBalance(command)
            }

        assertEquals("User not found with id $invalidUserId", exception.message)
    }
}
