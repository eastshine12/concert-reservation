package hhplus.concertreservation.interfaces.api.user.dto.res

data class PaymentHistoryResponse(
    val payments: List<PaymentDetail>
)

data class PaymentDetail(
    val id: Long,
    val title: String,
    val time: String,
    val seatNumber: String,
    val price: Int,
    val status: String,
)
