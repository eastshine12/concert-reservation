package hhplus.concertreservation.interfaces.api.payment.dto.req

import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand

data class PaymentRequest(
    val userId: Long,
    val reservationId: Long,
) {
    fun toCommand(token: String): PaymentCommand {
        return PaymentCommand(
            userId = userId,
            reservationId = reservationId,
            token = token,
        )
    }
}
