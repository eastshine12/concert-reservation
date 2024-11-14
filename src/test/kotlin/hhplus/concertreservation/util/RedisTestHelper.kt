package hhplus.concertreservation.util

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.infrastructure.repository.waitingQueue.WaitingQueueRedisRepository.Companion.ACTIVE_TOKEN_PREFIX
import hhplus.concertreservation.infrastructure.repository.waitingQueue.WaitingQueueRedisRepository.Companion.TOKEN_INFO_PREFIX
import hhplus.concertreservation.infrastructure.repository.waitingQueue.WaitingQueueRedisRepository.Companion.WAITING_TOKEN_PREFIX
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset

class RedisTestHelper(private val redisTemplate: RedisTemplate<String, Any>) {
    fun saveTokenAndInfo(waitingQueue: WaitingQueue) {
        val expiresAt = LocalDateTime.now().plusMinutes(10).toEpochSecond(ZoneOffset.UTC).toDouble()
        if (waitingQueue.status == QueueStatus.WAITING) {
            redisTemplate.opsForZSet().add(
                "$WAITING_TOKEN_PREFIX:${waitingQueue.scheduleId}",
                waitingQueue.token,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toDouble(),
            )
        } else {
            redisTemplate.opsForZSet().add(
                "$ACTIVE_TOKEN_PREFIX:${waitingQueue.scheduleId}",
                waitingQueue.token,
                expiresAt,
            )
        }
        val tokenInfoKey = "$TOKEN_INFO_PREFIX:${waitingQueue.token}"
        val fields =
            mapOf(
                "scheduleId" to waitingQueue.scheduleId.toString(),
                "status" to waitingQueue.status.name,
                "expiresAt" to LocalDateTime.ofEpochSecond(expiresAt.toLong(), 0, ZoneOffset.UTC).toString(),
            )

        redisTemplate.opsForHash<String, String>().putAll(tokenInfoKey, fields)
    }
}
