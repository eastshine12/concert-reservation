package hhplus.concertreservation.domain.payment.dto.command

data class PaymentCommand(
    val token: String,
    val userId: Long,
    val reservationId: Long,
)
