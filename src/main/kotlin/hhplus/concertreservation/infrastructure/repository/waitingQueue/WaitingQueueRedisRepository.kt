package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.*

@Primary
@Repository
class WaitingQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
) : WaitingQueueRepository {
    override fun save(waitingQueue: WaitingQueue): WaitingQueue {
        redisTemplate.opsForZSet().add(
            "WaitingToken:${waitingQueue.scheduleId}",
            waitingQueue.token,
            System.currentTimeMillis().toDouble(),
        )
        redisTemplate.expire("WaitingToken:${waitingQueue.scheduleId}", Duration.ofHours(1))
        redisTemplate.opsForHash<String, String>().put(
            "TokenScheduleMap",
            waitingQueue.token,
            waitingQueue.scheduleId.toString(),
        )
        redisTemplate.expire("TokenScheduleMap", Duration.ofHours(1))
        return waitingQueue
    }

    override fun findWaitingQueue(
        token: String,
        scheduleId: Long,
    ): WaitingQueue? {
        return findInWaitingToken(token, scheduleId) ?: findInActiveToken(token, scheduleId)
    }

    override fun findScheduleIdByToken(token: String): Long? {
        val scheduleId = redisTemplate.opsForHash<String, String>().get("TokenScheduleMap", token)
        return scheduleId?.toLong()
    }

    override fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue> {
        TODO("Not yet implemented")
    }

    override fun findByStatus(status: QueueStatus): List<WaitingQueue> {
        TODO("Not yet implemented")
    }

    override fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue> {
        TODO("Not yet implemented")
    }

    override fun delete(waitingQueue: WaitingQueue) {
        val activeKey = "ActiveToken:${waitingQueue.scheduleId}"
        redisTemplate.opsForZSet().remove(activeKey, waitingQueue.token)
    }

    override fun getAllWaitingTokenKeys(): MutableSet<String> {
        return redisTemplate.keys("WaitingToken:*")
    }

    override fun getAllActiveTokenKeys(): MutableSet<String> {
        return redisTemplate.keys("ActiveToken:*")
    }

    override fun getTopWaitingTokens(
        scheduleId: Long,
        maxTokens: Int,
    ): Set<Any> {
        val key = "WaitingToken:$scheduleId"
        return redisTemplate.opsForZSet().range(key, 0, (maxTokens - 1).toLong()) ?: emptySet()
    }

    override fun removeWaitingTokens(
        scheduleId: Long,
        tokens: Set<Any>,
    ) {
        val key = "WaitingToken:$scheduleId"
        redisTemplate.opsForZSet().remove(key, *tokens.toTypedArray())
    }

    override fun removeExpiredTokens(scheduleId: Long) {
        val activeKey = "ActiveToken:$scheduleId"
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toDouble()
        redisTemplate.opsForZSet().removeRangeByScore(activeKey, 0.0, currentTime)
    }

    override fun addActiveTokens(
        scheduleId: Long,
        tokens: Set<Any>,
        expiresInMinutes: Long,
    ) {
        val key = "ActiveToken:$scheduleId"
        val expiresAt = LocalDateTime.now().plusMinutes(expiresInMinutes).toEpochSecond(ZoneOffset.UTC).toDouble()
        tokens.forEach { token ->
            redisTemplate.opsForZSet().add(key, token, expiresAt)
        }
    }

    private fun findInWaitingToken(
        token: String,
        scheduleId: Long,
    ): WaitingQueue? {
        val waitingKey = "WaitingToken:$scheduleId"
        val rank = redisTemplate.opsForZSet().rank(waitingKey, token)?.plus(1) ?: return null
        return WaitingQueue(
            token = token,
            scheduleId = scheduleId,
            status = QueueStatus.PENDING,
            position = rank.toInt(),
        )
    }

    private fun findInActiveToken(
        token: String,
        scheduleId: Long,
    ): WaitingQueue? {
        val activeKey = "ActiveToken:$scheduleId"
        val score = redisTemplate.opsForZSet().score(activeKey, token) ?: return null
        return WaitingQueue(
            token = token,
            scheduleId = scheduleId,
            status = QueueStatus.ACTIVE,
            expiresAt = toLocalDateTime(score),
        )
    }

    private fun toLocalDateTime(score: Double): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(score.toLong()), ZoneOffset.UTC)
    }
}
