package hhplus.concertreservation.interfaces.api.payment.dto.res

import hhplus.concertreservation.application.payment.dto.info.PaymentInfo
import java.math.BigDecimal

data class PaymentResponse(
    val paymentId: Long,
    val amount: BigDecimal,
    val status: String,
) {
    companion object {
        fun fromInfo(paymentInfo: PaymentInfo): PaymentResponse {
            return PaymentResponse(
                paymentId = paymentInfo.paymentId,
                amount = paymentInfo.amount,
                status = paymentInfo.status,
            )
        }
    }
}
