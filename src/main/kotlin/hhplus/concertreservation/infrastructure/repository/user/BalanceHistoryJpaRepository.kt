package hhplus.concertreservation.infrastructure.repository.user

import hhplus.concertreservation.domain.user.entity.BalanceHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceHistoryJpaRepository : JpaRepository<BalanceHistory, Long> {
    fun findAllByUserId(userId: Long): List<BalanceHistory>
}
