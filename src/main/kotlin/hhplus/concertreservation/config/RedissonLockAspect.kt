package hhplus.concertreservation.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Aspect
@Component
class RedissonLockAspect(
    private val redissonClient: RedissonClient,
) {
    @Around("@annotation(distributedLock)")
    fun around(
        joinPoint: ProceedingJoinPoint,
        distributedLock: DistributedLock,
    ): Any {
        val lockKey = distributedLock.key
        val lock = redissonClient.getLock(lockKey)

        return try {
            lock.lock() // 락 획득
            joinPoint.proceed() // 원래 메서드 실행
        } finally {
            lock.unlock() // 락 해제
        }
    }
}
