import hhplus.concertreservation.ConcertReservationApplication
import hhplus.concertreservation.IntegrationTestBase
import hhplus.concertreservation.application.waitingQueue.WaitingQueueFacade
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.dto.command.TokenCommand
import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.domain.waitingQueue.dto.info.WaitingQueueInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [ConcertReservationApplication::class])
class WaitingQueueFacadeIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var waitingQueueFacade: WaitingQueueFacade
    private lateinit var concert: Concert
    private lateinit var schedule: ConcertSchedule
    private lateinit var waitingQueue: WaitingQueue
    private lateinit var waitingQueue2: WaitingQueue
    private lateinit var waitingQueue3: WaitingQueue

    @BeforeEach
    fun setUp() {
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
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule.id,
                    token = "123e4567-e89b-12d3-a456-426614174000",
                    status = QueueStatus.PENDING,
                    queuePosition = 10,
                    expiresAt = null,
                ),
            )
        waitingQueue2 =
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule.id,
                    token = "123e4567-e89b-12d3-a456-426614174001",
                    status = QueueStatus.PENDING,
                    queuePosition = 11,
                    expiresAt = null,
                ),
            )
        waitingQueue3 =
            waitingQueueJpaRepository.save(
                WaitingQueue(
                    scheduleId = schedule.id,
                    token = "123e4567-e89b-12d3-a456-426614174002",
                    status = QueueStatus.PENDING,
                    queuePosition = 12,
                    expiresAt = null,
                ),
            )
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
        assertEquals(13, tokenInfo.queuePosition)
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
        assertEquals(2, waitingQueueInfo.remainingPosition)
        assertEquals(QueueStatus.PENDING, waitingQueueInfo.status)
    }
}
