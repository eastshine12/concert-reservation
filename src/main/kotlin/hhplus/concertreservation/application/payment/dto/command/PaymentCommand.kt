package hhplus.concertreservation.application.payment.dto.command

data class PaymentCommand(
    val token: String,
    val userId: Long,
    val reservationId: Long,
)
