package hhplus.concertreservation.domain.user.service

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.dto.info.UpdateBalanceInfo
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.exception.UserNotFoundException
import hhplus.concertreservation.domain.user.repository.BalanceHistoryRepository
import hhplus.concertreservation.domain.user.repository.UserRepository
import hhplus.concertreservation.domain.user.toUpdateBalanceInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UserService(
    private val userRepository: UserRepository,
    private val balanceHistoryRepository: BalanceHistoryRepository,
) {
    fun getByUserId(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User not found with id $userId")
    }

    fun getUserBalance(userId: Long): BigDecimal {
        return getByUserId(userId).balance
    }

    @Transactional
    fun updateUserBalance(
        userId: Long,
        amount: BigDecimal,
        type: PointTransactionType,
    ): UpdateBalanceInfo {
        val user: User = getByUserId(userId)
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
