package hhplus.concertreservation.interfaces.api.payment.dto.res

import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import java.math.BigDecimal

data class PaymentHistoryResponse(
    val payments: List<PaymentDetail>,
) {
    companion object {
        fun fromInfoList(paymentInfoList: List<PaymentInfo>): PaymentHistoryResponse {
            return PaymentHistoryResponse(
                payments =
                    paymentInfoList.map { paymentInfo ->
                        PaymentDetail(
                            id = paymentInfo.paymentId,
                            price = paymentInfo.amount,
                            status = paymentInfo.status,
                        )
                    },
            )
        }
    }
}

data class PaymentDetail(
    val id: Long?,
    val price: BigDecimal,
    val status: String,
)
