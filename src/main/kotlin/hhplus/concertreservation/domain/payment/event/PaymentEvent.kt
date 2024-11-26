package hhplus.concertreservation.domain.payment.event

import hhplus.concertreservation.domain.payment.dto.command.PaymentCommand
import java.math.BigDecimal

class PaymentEvent {
    data class Initiated(
        val userId: Long,
        val reservationId: Long,
        val price: BigDecimal,
        val token: String,
    ) {
        companion object {
            fun from(
                command: PaymentCommand,
                price: BigDecimal,
            ): Initiated {
                return Initiated(
                    userId = command.userId,
                    reservationId = command.reservationId,
                    price = price,
                    token = command.token,
                )
            }
        }
    }
}
