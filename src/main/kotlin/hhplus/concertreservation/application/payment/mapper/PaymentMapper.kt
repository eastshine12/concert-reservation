package hhplus.concertreservation.application.payment.mapper

import hhplus.concertreservation.application.payment.dto.info.PaymentInfo
import hhplus.concertreservation.domain.payment.Payment
import org.springframework.stereotype.Component

@Component
class PaymentMapper {

    fun toPaymentInfo(payment: Payment): PaymentInfo {
        return PaymentInfo(
            paymentId = payment.id,
            amount = payment.amount,
            status = payment.status.name
        )
    }
}
