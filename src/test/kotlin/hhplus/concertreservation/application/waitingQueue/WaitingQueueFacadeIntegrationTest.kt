import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.waitingQueue.WaitingQueueFacade
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.dto.command.TokenCommand
import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.domain.waitingQueue.dto.info.WaitingQueueInfo
import hhplus.concertreservation.util.RedisTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [ConcertReservationApplication::class])
class WaitingQueueFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var waitingQueueFacade: WaitingQueueFacade
    private lateinit var redisTestHelper: RedisTestHelper
    private lateinit var concert: Concert
    private lateinit var schedule: ConcertSchedule
    private lateinit var waitingQueue: WaitingQueue
    private lateinit var waitingQueue2: WaitingQueue
    private lateinit var waitingQueue3: WaitingQueue

    @BeforeEach
    fun setUp() {
        redisTestHelper = RedisTestHelper(redisTemplate)
        concert =
            concertJpaRepository.save(
                Concert(
                    title = "concert",
                    duration = 120,
                ),
            )
        schedule =
            concertScheduleJpaRepository.save(
                ConcertSchedule(
                    concertId = concert.id,
                    startTime = LocalDateTime.now().plusDays(1),
                    totalSeats = 5,
                    availableSeats = 5,
                ),
            )

        waitingQueue =
            WaitingQueue(
                scheduleId = schedule.id,
                token = "123e4567-e89b-12d3-a456-426614174000",
                status = QueueStatus.WAITING,
                expiresAt = null,
            )
        waitingQueue2 =
            WaitingQueue(
                scheduleId = schedule.id,
                token = "123e4567-e89b-12d3-a456-426614174001",
                status = QueueStatus.WAITING,
                expiresAt = null,
            )
        waitingQueue3 =
            WaitingQueue(
                scheduleId = schedule.id,
                token = "123e4567-e89b-12d3-a456-426614174002",
                status = QueueStatus.WAITING,
                expiresAt = null,
            )

        redisTestHelper.saveTokenAndInfo(waitingQueue)
        redisTestHelper.saveTokenAndInfo(waitingQueue2)
        redisTestHelper.saveTokenAndInfo(waitingQueue3)
    }

    @Test
    fun `must generate token successfully`() {
        // Given
        val tokenCommand =
            TokenCommand(
                concertId = concert.id,
                concertScheduleId = schedule.id,
                token = null,
                userId = 1L,
            )

        // When
        val tokenInfo: TokenInfo = waitingQueueFacade.issueWaitingQueueToken(tokenCommand)

        // Then
        assertNotNull(tokenInfo)
        assertEquals(schedule.id, tokenInfo.scheduleId)
        assertNotNull(tokenInfo.token)
    }

    @Test
    fun `must return waiting queue status`() {
        // Given
        val token = "123e4567-e89b-12d3-a456-426614174002" // waitingQueue3

        // When
        val waitingQueueInfo: WaitingQueueInfo = waitingQueueFacade.getWaitingQueueStatus(token)

        // Then
        assertNotNull(waitingQueueInfo)
        assertEquals(3, waitingQueueInfo.remainingPosition)
        assertEquals(QueueStatus.WAITING, waitingQueueInfo.status)
    }

    @Test
    fun `should throw exception when concert schedule id is invalid during token issuance`() {
        // Given
        val invalidScheduleId = 999L // 존재하지 않는 일정 ID
        val tokenCommand =
            TokenCommand(
                concertId = concert.id,
                concertScheduleId = invalidScheduleId,
                token = null,
                userId = 1L,
            )

        // When & Then
        val exception =
            assertThrows<CoreException> {
                waitingQueueFacade.issueWaitingQueueToken(tokenCommand)
            }

        assertEquals("No concert schedule found for the given ID.", exception.message)
    }

    @Test
    fun `should throw exception when token already exists in waiting queue`() {
        // Given
        val existingToken = "123e4567-e89b-12d3-a456-426614174000"

        val tokenCommand =
            TokenCommand(
                concertId = concert.id,
                concertScheduleId = schedule.id,
                token = existingToken,
                userId = 1L,
            )

        // When & Then
        val exception =
            assertThrows<CoreException> {
                waitingQueueFacade.issueWaitingQueueToken(tokenCommand)
            }

        assertEquals("A queue already exists for this token.", exception.message)
    }

    @Test
    fun `should throw exception when token is invalid during waiting queue status retrieval`() {
        // Given
        val invalidToken = "invalid-token"

        // When & Then
        val exception =
            assertThrows<CoreException> {
                waitingQueueFacade.getWaitingQueueStatus(invalidToken)
            }

        assertEquals("Invalid or missing token.", exception.message)
    }

    @Test
    fun `should issue unique queue positions when multiple requests are made concurrently`() {
        // Given
        val scheduleId = 1L
        val userIds = (1L..10L).toList()
        val executor: ExecutorService = Executors.newFixedThreadPool(10)

        // When
        val tasks =
            userIds.map { userId ->
                Callable {
                    waitingQueueFacade.issueWaitingQueueToken(
                        TokenCommand(
                            concertId = concert.id,
                            concertScheduleId = scheduleId,
                            token = null,
                            userId = userId,
                        ),
                    )
                }
            }

        val results = executor.invokeAll(tasks)
        executor.shutdown()

        // Then
        assertEquals(10, results.size)
    }
}
