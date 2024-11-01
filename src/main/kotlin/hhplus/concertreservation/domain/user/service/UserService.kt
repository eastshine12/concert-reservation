package hhplus.concertreservation.domain.user.service

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.repository.BalanceHistoryRepository
import hhplus.concertreservation.domain.user.repository.UserRepository
import hhplus.concertreservation.domain.user.toUpdateBalanceInfo
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UserService(
    private val userRepository: UserRepository,
    private val balanceHistoryRepository: BalanceHistoryRepository,
) {
    fun checkUserExists(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw CoreException(
                errorType = ErrorType.USER_NOT_FOUND,
                details =
                    mapOf(
                        "userId" to userId,
                    ),
            )
    }

    fun getUserBalance(userId: Long): BigDecimal {
        return checkUserExists(userId).balance
    }

    @Transactional
    @Retryable(
        value = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100),
    )
    fun updateUserBalance(
        userId: Long,
        amount: BigDecimal,
        type: PointTransactionType,
    ): UpdateBalanceInfo {
        val user: User = checkUserExists(userId)
        when (type) {
            PointTransactionType.CHARGE -> user.charge(amount)
            PointTransactionType.USE -> user.use(amount)
        }
        val balanceHistory =
            balanceHistoryRepository.save(
                BalanceHistory.create(userId, amount, type),
            )
        return balanceHistory.toUpdateBalanceInfo(success = true)
    }
}
