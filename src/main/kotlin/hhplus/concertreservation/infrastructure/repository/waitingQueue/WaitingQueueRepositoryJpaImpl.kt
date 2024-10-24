package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import org.springframework.stereotype.Repository

@Repository
class WaitingQueueRepositoryJpaImpl(
    private val waitingQueueJpaRepository: WaitingQueueJpaRepository,
) : WaitingQueueRepository {
    override fun save(waitingQueue: WaitingQueue): WaitingQueue {
        return waitingQueueJpaRepository.save(waitingQueue)
    }

    override fun findByToken(token: String): WaitingQueue? {
        return waitingQueueJpaRepository.findByToken(token)
    }

    override fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue> {
        return waitingQueueJpaRepository.findAllByScheduleId(scheduleId)
    }

    override fun findMaxQueuePositionByScheduleId(scheduleId: Long): Int {
        return waitingQueueJpaRepository.findMaxQueuePositionByScheduleId(scheduleId)
    }

    override fun findByStatus(status: QueueStatus): List<WaitingQueue> {
        return waitingQueueJpaRepository.findByStatus(status)
    }

    override fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue> {
        return waitingQueueJpaRepository.saveAll(queues)
    }
}
