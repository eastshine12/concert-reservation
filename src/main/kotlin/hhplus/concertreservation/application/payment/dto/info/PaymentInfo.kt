package hhplus.concertreservation.application.payment.dto.info

import java.math.BigDecimal

data class PaymentInfo(
    val paymentId: Long,
    val amount: BigDecimal,
    val status: String
)
