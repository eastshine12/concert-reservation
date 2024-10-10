package hhplus.concertreservation.interfaces.api.payment.dto.req

data class PaymentRequest(
    val userId: Long,
    val amount: Int,
)
