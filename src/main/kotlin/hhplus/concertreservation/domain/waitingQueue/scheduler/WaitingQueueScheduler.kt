package hhplus.concertreservation.domain.waitingQueue.scheduler

import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WaitingQueueScheduler(
    private val queueManager: QueueManager,
) {
    @Scheduled(fixedRateString = "\${waiting-queue.activateRate}")
    fun activateWaitingQueue() {
        queueManager.activatePendingQueues()
    }

    @Scheduled(fixedRateString = "\${waiting-queue.expireCheckRate}")
    fun expireWaitingQueues() {
        queueManager.expireActiveQueues()
    }
}
