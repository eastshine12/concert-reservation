package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.config.WaitingQueueProperties
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class QueueManagerTest {
    private val waitingQueueRepository = mockk<WaitingQueueRepository>(relaxed = true)
    private val waitingQueueProperties = mockk<WaitingQueueProperties>(relaxed = true)
    private val queueManager = QueueManager(waitingQueueRepository, waitingQueueProperties)

    @Test
    fun `should enqueue successfully`() {
        // given
        val concertSchedule = mockk<ConcertSchedule>(relaxed = true)
        val token = "token123"
        val waitingQueue =
            WaitingQueue(
                scheduleId = concertSchedule.id,
                token = token,
                status = QueueStatus.WAITING,
                expiresAt = null,
            )

        every { waitingQueueRepository.addWaitingQueue(any()) } returns waitingQueue

        // when
        val result = queueManager.enqueue(concertSchedule.id, token)

        // then
        assertEquals(waitingQueue, result)
    }

    @Test
    fun `must throw exception when token is not active`() {
        // given
        val waitingQueue =
            mockk<WaitingQueue> {
                every { status } returns QueueStatus.ACTIVE
                every { token } returns "token123"
                every { expiresAt } returns LocalDateTime.now().minusMinutes(10)
            }

        // when, then
        assertThrows<CoreException> {
            queueManager.validateTokenState(waitingQueue)
        }
    }

    @Test
    fun `must throw exception when token has expired`() {
        // given
        val waitingQueue =
            mockk<WaitingQueue> {
                every { status } returns QueueStatus.ACTIVE
                every { expiresAt } returns LocalDateTime.now().minusMinutes(1)
                every { token } returns "token123"
            }

        // when, then
        assertThrows<CoreException> {
            queueManager.validateTokenState(waitingQueue)
        }
    }

    @Test
    fun `should activate pending queues`() {
        // given
        val allWaitingKeys = mutableSetOf("WaitingToken:1", "WaitingToken:2")
        val tokensToActivate = setOf("token1", "token2")

        every { waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.WAITING) } returns allWaitingKeys
        every { waitingQueueRepository.getTokensFromTopToRange(1L, any()) } returns tokensToActivate
        every { waitingQueueProperties.activeUsers } returns 2
        every { waitingQueueProperties.expireMinutes } returns 30

        // when
        queueManager.activatePendingQueues()

        // then
        verify { waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.WAITING) }
        verify { waitingQueueRepository.getTokensFromTopToRange(1L, 2) }
        verify { waitingQueueRepository.moveToActiveQueue(1L, tokensToActivate, 30) }
    }

    @Test
    fun `should expire active queues`() {
        // given
        val allActiveKeys = mutableSetOf("ActiveToken:1", "ActiveToken:2")

        every { waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.ACTIVE) } returns allActiveKeys

        // when
        queueManager.expireActiveQueues()

        // then
        verify { waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.ACTIVE) }
        verify { waitingQueueRepository.removeExpiredTokens(1L) }
        verify { waitingQueueRepository.removeExpiredTokens(2L) }
    }
}
