package hhplus.concertreservation.domain.user.service

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.component.BalanceManager
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.exception.UserNotFoundException
import hhplus.concertreservation.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UserService(
    private val userRepository: UserRepository,
    private val balanceManager: BalanceManager,
) {
    fun getByUserId(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User with id $userId not found.")
    }

    fun getUserBalance(userId: Long): BigDecimal {
        return getByUserId(userId).balance
    }

    @Transactional
    fun chargeUserBalance(userId: Long, amount: BigDecimal): BalanceHistory {
        getByUserId(userId).charge(amount)
        return balanceManager.saveBalanceHistory(userId, amount, PointTransactionType.CHARGE)
    }

    @Transactional
    fun deductUserBalance(userId: Long, amount: BigDecimal): BalanceHistory {
        getByUserId(userId).use(amount)
        return balanceManager.saveBalanceHistory(userId, amount, PointTransactionType.USE)
    }
}
