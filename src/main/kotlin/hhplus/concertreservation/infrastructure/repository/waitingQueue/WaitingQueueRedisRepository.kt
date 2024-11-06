package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import java.time.*

@Repository
class WaitingQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
) : WaitingQueueRepository {
    companion object {
        const val WAITING_TOKEN_PREFIX = "WaitingToken"
        const val ACTIVE_TOKEN_PREFIX = "ActiveToken"
        const val TOKEN_INFO_PREFIX = "TokenInfo"
    }

    override fun addWaitingQueue(waitingQueue: WaitingQueue): WaitingQueue {
        // 1. WaitingToken 저장
        redisTemplate.opsForZSet().add(
            "$WAITING_TOKEN_PREFIX:${waitingQueue.scheduleId}",
            waitingQueue.token,
            System.currentTimeMillis().toDouble(),
        )
        redisTemplate.expire("$WAITING_TOKEN_PREFIX:${waitingQueue.scheduleId}", Duration.ofHours(1))

        // 2. TokenInfo 저장
        val key = "$TOKEN_INFO_PREFIX:${waitingQueue.token}"
        val fields =
            mapOf(
                "scheduleId" to waitingQueue.scheduleId.toString(),
                "status" to waitingQueue.status.name,
            )
        redisTemplate.opsForHash<String, String>().putAll(key, fields)
        redisTemplate.expire(key, Duration.ofHours(1))
        return waitingQueue
    }

    override fun moveToActiveQueue(
        scheduleId: Long,
        tokens: Set<Any>,
        expiresInMinutes: Long,
    ) {
        val waitingKey = "$WAITING_TOKEN_PREFIX:$scheduleId"
        val activeKey = "$ACTIVE_TOKEN_PREFIX:$scheduleId"
        val expiresAt = LocalDateTime.now().plusMinutes(expiresInMinutes).toEpochSecond(ZoneOffset.UTC).toDouble()

        tokens.forEach { token ->
            // 1. WaitingToken 삭제
            redisTemplate.opsForZSet().remove(waitingKey, token)

            // 2. ActiveToken 저장
            redisTemplate.opsForZSet().add(activeKey, token, expiresAt)
            redisTemplate.expire(activeKey, Duration.ofHours(1))

            // 3. TokenInfo 변경
            val tokenInfoKey = "$TOKEN_INFO_PREFIX:$token"
            val fields =
                mapOf(
                    "status" to QueueStatus.ACTIVE.name,
                    "expiresAt" to LocalDateTime.ofEpochSecond(expiresAt.toLong(), 0, ZoneOffset.UTC).toString(),
                )
            redisTemplate.opsForHash<String, String>().putAll(tokenInfoKey, fields)
        }
    }

    override fun findByToken(token: String): WaitingQueue? {
        val key = "$TOKEN_INFO_PREFIX:$token"
        val values = redisTemplate.opsForHash<String, String>().entries(key)

        val scheduleId = values["scheduleId"]?.toLong() ?: return null
        val status = values["status"]?.let { QueueStatus.valueOf(it) } ?: return null
        val expiresAt = values["expiresAt"]?.let { LocalDateTime.parse(it) }

        return WaitingQueue(
            token = token,
            scheduleId = scheduleId,
            status = status,
            expiresAt = expiresAt,
        )
    }

    override fun getAllTokenKeysByStatus(status: QueueStatus): MutableSet<String> {
        val pattern =
            when (status) {
                QueueStatus.WAITING -> "$WAITING_TOKEN_PREFIX*"
                QueueStatus.ACTIVE -> "$ACTIVE_TOKEN_PREFIX*"
            }
        val keys = mutableSetOf<String>()
        val scanOptions: ScanOptions = ScanOptions.scanOptions().match(pattern).count(100).build()

        redisTemplate.execute { connection ->
            val cursor = connection.keyCommands().scan(scanOptions)
            cursor.forEachRemaining { key ->
                keys.add(String(key))
            }
        }

        return keys
    }

    override fun getTokensFromTopToRange(
        scheduleId: Long,
        maxTokens: Int,
    ): Set<Any> {
        val key = "$WAITING_TOKEN_PREFIX:$scheduleId"
        return redisTemplate.opsForZSet().range(key, 0, (maxTokens - 1).toLong()) ?: emptySet()
    }

    override fun getTokenRank(waitingQueue: WaitingQueue): Long? {
        val prefix =
            when (waitingQueue.status) {
                QueueStatus.WAITING -> WAITING_TOKEN_PREFIX
                QueueStatus.ACTIVE -> ACTIVE_TOKEN_PREFIX
            }
        return redisTemplate.opsForZSet().rank("$prefix:${waitingQueue.scheduleId}", waitingQueue.token)?.plus(1)
    }

    override fun getTokenScore(waitingQueue: WaitingQueue): Double? {
        val prefix =
            when (waitingQueue.status) {
                QueueStatus.WAITING -> WAITING_TOKEN_PREFIX
                QueueStatus.ACTIVE -> ACTIVE_TOKEN_PREFIX
            }
        return redisTemplate.opsForZSet().score("$prefix:${waitingQueue.scheduleId}", waitingQueue.token)
    }

    override fun remove(waitingQueue: WaitingQueue) {
        val prefix =
            when (waitingQueue.status) {
                QueueStatus.WAITING -> WAITING_TOKEN_PREFIX
                QueueStatus.ACTIVE -> ACTIVE_TOKEN_PREFIX
            }
        val key = "$prefix:${waitingQueue.scheduleId}"
        redisTemplate.opsForZSet().remove(key, waitingQueue.token)
    }

    override fun removeExpiredTokens(scheduleId: Long) {
        val activeKey = "$ACTIVE_TOKEN_PREFIX:$scheduleId"
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toDouble()
        redisTemplate.opsForZSet().removeRangeByScore(activeKey, 0.0, currentTime)
    }
}
