package hhplus.concertreservation.domain.user.component

import hhplus.concertreservation.domain.common.enums.PointTransactionType
import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.repository.BalanceHistoryRepository
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.repository.UserRepository
import hhplus.concertreservation.domain.user.exception.InvalidBalanceAmountException
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class BalanceManager(
    private val balanceHistoryRepository: BalanceHistoryRepository,
) {
    fun saveBalanceHistory(userId: Long, amount: BigDecimal, type: PointTransactionType): BalanceHistory {
        val balanceHistory = BalanceHistory(
            userId = userId,
            amount = amount,
            type = type,
        )
        return balanceHistoryRepository.save(balanceHistory)
    }
}
