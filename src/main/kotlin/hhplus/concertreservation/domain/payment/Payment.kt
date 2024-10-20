package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Payment(
    val userId: Long,
    val reservationId: Long,
    amount: BigDecimal,
    status: PaymentStatus,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    var amount: BigDecimal = amount
        protected set

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = status
        protected set

    companion object {
        fun create(
            userId: Long,
            reservationId: Long,
            amount: BigDecimal,
        ): Payment {
            return Payment(
                userId = userId,
                reservationId = reservationId,
                amount = amount,
                status = PaymentStatus.SUCCESS,
            )
        }
    }
}
