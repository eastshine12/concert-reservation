package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Payment(
    var userId: Long,
    var reservationId: Long,
    var amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity()
