package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
import hhplus.concertreservation.domain.waitingQueue.exception.QueueNotFoundException
import hhplus.concertreservation.domain.waitingQueue.exception.TokenAlreadyExistsException
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
        val token = "valid-token"

        every { tokenManager.validateAndGetToken(token) } returns token
        every { queueManager.findQueueByToken(token) } returns waitingQueue

        // when
        val result = waitingQueueService.issueToken(token, schedule)

        // then
        assertEquals(waitingQueue, result)
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
            }

        // when & then
        val exception =
            assertThrows<TokenAlreadyExistsException> {
                waitingQueueService.issueToken(token, schedule)
            }

        assertEquals("Token already exists for this schedule.", exception.message)
    }

    @Test
    fun `must throw exception when token is invalid`() {
        // given
        val invalidToken = "invalid-token"

        every { tokenManager.validateAndGetToken(invalidToken) } throws InvalidTokenException("Invalid token format")

        // when / then
        assertThrows<InvalidTokenException> {
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
        assertThrows<QueueNotFoundException> {
            waitingQueueService.validateAndGetToken(token)
        }
    }

    @Test
    fun `should calculate remaining position`() {
        // given
        val scheduleId = 1L
        val myPosition = 7

        every { waitingQueueRepository.findMinQueuePositionByScheduleId(scheduleId) } returns 3

        // when
        val result = waitingQueueService.calculateRemainingPosition(scheduleId, myPosition)

        // then
        assertEquals(4, result)
    }
}
