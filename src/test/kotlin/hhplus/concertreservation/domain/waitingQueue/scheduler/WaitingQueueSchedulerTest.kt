package hhplus.concertreservation.domain.waitingQueue.scheduler

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WaitingQueueSchedulerTest {
    private val waitingQueueRepository = mockk<WaitingQueueRepository>(relaxed = true)
    private val queueManager = mockk<QueueManager>(relaxed = true)
    private val waitingQueueScheduler = WaitingQueueScheduler(waitingQueueRepository, queueManager)

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
            )
        val activeQueues =
            listOf(
                WaitingQueue(
                    scheduleId = 1L,
                    token = "token2",
                    status = QueueStatus.ACTIVE,
                    expiresAt = null,
                ),
            )
        every { waitingQueueRepository.findByStatus(QueueStatus.PENDING) } returns pendingQueues
        every { waitingQueueRepository.findByStatus(QueueStatus.ACTIVE) } returns activeQueues
        every { queueManager.countActiveQueuesByScheduleId(activeQueues) } returns mutableMapOf(1L to 1)

        // when
        waitingQueueScheduler.activateWaitingQueue()

        // then
        verify { queueManager.activatePendingQueues(pendingQueues, mutableMapOf(1L to 1)) }
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
                    expiresAt = LocalDateTime.now().minusMinutes(1),
                ),
            )
        every { waitingQueueRepository.findByStatus(QueueStatus.ACTIVE) } returns expiredQueues

        // when
        waitingQueueScheduler.expireWaitingQueues()

        // then
        verify { queueManager.expireActiveQueues(expiredQueues) }
    }
}
