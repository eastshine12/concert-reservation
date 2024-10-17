package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
import hhplus.concertreservation.domain.waitingQueue.exception.QueueNotFoundException
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
    fun `should get or generate token`() {
        // given
        val schedule = mockk<ConcertSchedule>(relaxed = true)
        val waitingQueue = mockk<WaitingQueue>(relaxed = true)
        val token = "valid-token"

        every { tokenManager.validateAndGetToken(token) } returns token
        every { queueManager.findQueueByToken(token) } returns waitingQueue

        // when
        val result = waitingQueueService.getOrGenerateToken(token, schedule)

        // then
        assertEquals(waitingQueue, result)
    }

    @Test
    fun `should generate token if no token is provided`() {
        // given
        val schedule = mockk<ConcertSchedule>(relaxed = true)
        val waitingQueue = mockk<WaitingQueue>(relaxed = true)

        every { tokenManager.generateToken() } returns "generated-token"
        every { queueManager.calculateQueuePosition(schedule.id) } returns 1
        every { queueManager.enqueue(schedule, "generated-token", 1) } returns waitingQueue

        // when
        val result = waitingQueueService.getOrGenerateToken(null, schedule)

        // then
        assertEquals(waitingQueue, result)
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
