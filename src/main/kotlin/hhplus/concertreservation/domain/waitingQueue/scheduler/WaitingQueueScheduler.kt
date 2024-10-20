package hhplus.concertreservation.domain.waitingQueue.scheduler

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WaitingQueueScheduler(
    private val waitingQueueRepository: WaitingQueueRepository,
    private val queueManager: QueueManager,
) {
    @Scheduled(fixedRateString = "\${waiting-queue.activationRate}")
    fun activateWaitingQueue() {
        val pendingQueues = waitingQueueRepository.findByStatus(QueueStatus.PENDING)
        val activeQueues = waitingQueueRepository.findByStatus(QueueStatus.ACTIVE)
        val activeCountMap: MutableMap<Long, Int> = queueManager.countActiveQueuesByScheduleId(activeQueues)
        queueManager.activatePendingQueues(pendingQueues, activeCountMap)
    }

    @Scheduled(fixedRateString = "\${waiting-queue.expirationRate}")
    fun expireWaitingQueues() {
        val expiredQueues =
            waitingQueueRepository.findByStatus(QueueStatus.ACTIVE)
                .filter { it.expiresAt?.isBefore(LocalDateTime.now()) ?: false }
        queueManager.expireActiveQueues(expiredQueues)
    }
}
