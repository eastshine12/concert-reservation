package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class WaitingQueueServiceTest {
    private val tokenManager = mockk<TokenManager>()
    private val queueManager = mockk<QueueManager>()
    private val waitingQueueRepository = mockk<WaitingQueueRepository>()
    private val waitingQueueService = WaitingQueueService(tokenManager, queueManager, waitingQueueRepository)

    @Test
    fun `should generate token`() {
        // given
        val schedule = mockk<ConcertSchedule>(relaxed = true)
        val waitingQueue = mockk<WaitingQueue>(relaxed = true)
        val tokenInfo = waitingQueue.toTokenInfo()

        every { queueManager.enqueue(any(), any(), any()) } returns waitingQueue
        every { tokenManager.generateToken() } returns "token"
        every { queueManager.calculateQueuePosition(scheduleId = schedule.id) } returns 1

        // when
        val result = waitingQueueService.issueToken(token = null, scheduleId = schedule.id)

        // then
        assertEquals(tokenInfo, result)
    }

    @Test
    fun `should throw exception if token is already provided`() {
        // given
        val schedule = mockk<ConcertSchedule>(relaxed = true)
        val token = "existing-token"

        every { tokenManager.validateAndGetToken(token) } returns token
        every { queueManager.findQueueByToken(token) } returns
            mockk {
                every { scheduleId } returns schedule.id
                every { status } returns QueueStatus.ACTIVE
            }

        // when & then
        val exception =
            assertThrows<CoreException> {
                waitingQueueService.issueToken(token, schedule.id)
            }

        assertEquals("A queue already exists for this token.", exception.message)
    }

    @Test
    fun `must throw exception when token is invalid`() {
        // given
        val invalidToken = "invalid-token"

        every { tokenManager.validateAndGetToken(invalidToken) } throws CoreException(ErrorType.INVALID_TOKEN)

        // when / then
        assertThrows<CoreException> {
            waitingQueueService.validateAndGetToken(invalidToken)
        }
    }

    @Test
    fun `must throw exception when queue not found for token`() {
        // given
        val token = "valid-token"

        every { tokenManager.validateAndGetToken(token) } returns token
        every { queueManager.findQueueByToken(token) } returns null

        // when / then
        assertThrows<CoreException> {
            waitingQueueService.validateAndGetToken(token)
        }
    }

    @Test
    fun `should calculate remaining position`() {
        // given
        val scheduleId = 1L
        val myPosition = 3
        val waitingQueues =
            listOf(
                WaitingQueue(1L, "token1", QueueStatus.PENDING, 1, expiresAt = null),
                WaitingQueue(2L, "token2", QueueStatus.PENDING, 2, expiresAt = null),
                WaitingQueue(3L, "token3", QueueStatus.PENDING, 3, expiresAt = null),
            )

        every { waitingQueueRepository.findAllByScheduleId(scheduleId) } returns waitingQueues

        // when
        val result = waitingQueueService.calculateRemainingPosition(scheduleId, myPosition)

        // then
        assertEquals(3, result)
    }
}
