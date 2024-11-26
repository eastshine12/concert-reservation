package hhplus.concertreservation.domain.payment.dto.info

import java.math.BigDecimal

data class PaymentInfo(
    val paymentId: Long? = null,
    val amount: BigDecimal,
    val status: String,
)
