package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.application.payment.dto.info.PaymentInfo

fun Payment.toPaymentInfo(): PaymentInfo {
    return PaymentInfo(
        paymentId = this.id,
        amount = this.amount,
        status = this.status.name
    )
}
