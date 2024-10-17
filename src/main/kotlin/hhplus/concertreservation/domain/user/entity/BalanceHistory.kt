package hhplus.concertreservation.domain.user.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.PointTransactionType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class BalanceHistory(
    var userId: Long,
    var amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    var type: PointTransactionType,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity()
