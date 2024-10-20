package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.PointTransactionType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class BalanceHistory(
    val userId: Long,
    val amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    val type: PointTransactionType,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    companion object {
        fun create(
            userId: Long,
            amount: BigDecimal,
            type: PointTransactionType,
        ): BalanceHistory {
            return BalanceHistory(
                userId = userId,
                amount = amount,
                type = type,
            )
        }
    }
}
