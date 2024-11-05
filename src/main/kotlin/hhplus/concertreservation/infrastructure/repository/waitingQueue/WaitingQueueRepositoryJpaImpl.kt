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

    override fun findWaitingQueue(
        token: String,
        scheduleId: Long,
    ): WaitingQueue? {
        return waitingQueueJpaRepository.findByToken(token)
    }

    override fun findScheduleIdByToken(token: String): Long? {
        TODO("Not yet implemented")
    }

    override fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue> {
        return waitingQueueJpaRepository.findAllByScheduleId(scheduleId)
    }

    override fun findByStatus(status: QueueStatus): List<WaitingQueue> {
        return waitingQueueJpaRepository.findByStatus(status)
    }

    override fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue> {
        return waitingQueueJpaRepository.saveAll(queues)
    }

    override fun delete(waitingQueue: WaitingQueue) {
        waitingQueueJpaRepository.save(waitingQueue)
    }

    override fun getAllWaitingTokenKeys(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getAllActiveTokenKeys(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getTopWaitingTokens(
        scheduleId: Long,
        maxTokens: Int,
    ): Set<Any> {
        TODO("Not yet implemented")
    }

    override fun removeWaitingTokens(
        scheduleId: Long,
        tokens: Set<Any>,
    ) {
        TODO("Not yet implemented")
    }

    override fun removeExpiredTokens(scheduleId: Long) {
        TODO("Not yet implemented")
    }

    override fun addActiveTokens(
        scheduleId: Long,
        tokens: Set<Any>,
        expiresInMinutes: Long,
    ) {
        TODO("Not yet implemented")
    }
}
