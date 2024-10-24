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
                status = QueueStatus.PENDING,
                expiresAt = null,
            )

        every { waitingQueueRepository.save(any()) } returns waitingQueue

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
                every { status } returns QueueStatus.EXPIRED
                every { token } returns "token123"
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
    fun `should count active queues by schedule id`() {
        // given
        val activeQueues =
            listOf(
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token1",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token2",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
                WaitingQueue(
                    scheduleId = 2L,
                    token = "token3",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
            )

        // when
        val result = queueManager.countActiveQueuesByScheduleId(activeQueues)

        // then
        assertEquals(2, result[1L])
        assertEquals(1, result[2L])
    }

    @Test
    fun `should activate pending queues`() {
        // given
        val pendingQueues =
            listOf(
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token1",
                    status = QueueStatus.PENDING,
                    expiresAt = null,
                ),
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token2",
                    status = QueueStatus.PENDING,
                    expiresAt = null,
                ),
            )
        val activeCountMap = mutableMapOf(1L to 0)
        every { waitingQueueProperties.maxActiveUsers } returns 2
        every { waitingQueueProperties.expireMinutes } returns 30

        // when
        queueManager.activatePendingQueues(pendingQueues, activeCountMap)

        // then
        verify { waitingQueueRepository.saveAll(any()) }
    }

    @Test
    fun `should expire active queues`() {
        // given
        val expiredQueues =
            listOf(
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token1",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token2",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
            )

        // when
        queueManager.expireActiveQueues(expiredQueues)

        // then
        verify { waitingQueueRepository.saveAll(expiredQueues) }
    }
}
