package hhplus.concertreservation.domain.user.repository

import hhplus.concertreservation.domain.user.entity.BalanceHistory

interface BalanceHistoryRepository {
    fun save(balanceHistory: BalanceHistory): BalanceHistory
    fun findAllByUserId(userId: Long): List<BalanceHistory>
}
