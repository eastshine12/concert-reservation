package hhplus.concertreservation.interfaces.api.payment.dto.res

data class PaymentResponse(
    val paymentId: Long,
    val amount: Int,
    val status: String,
)
