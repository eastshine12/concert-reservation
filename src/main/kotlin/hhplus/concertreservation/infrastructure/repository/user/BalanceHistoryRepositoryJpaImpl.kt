package hhplus.concertreservation.infrastructure.repository.user

import hhplus.concertreservation.domain.user.entity.BalanceHistory
import hhplus.concertreservation.domain.user.repository.BalanceHistoryRepository
import org.springframework.stereotype.Repository

@Repository
class BalanceHistoryRepositoryJpaImpl(
    private val balanceHistoryJpaRepository: BalanceHistoryJpaRepository,
) : BalanceHistoryRepository {
    override fun save(balanceHistory: BalanceHistory): BalanceHistory {
        return balanceHistoryJpaRepository.save(balanceHistory)
    }

    override fun findAllByUserId(userId: Long): List<BalanceHistory> {
        return balanceHistoryJpaRepository.findAllByUserId(userId)
    }
}
