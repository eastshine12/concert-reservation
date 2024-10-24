package hhplus.concertreservation.domain.common.enums

enum class PaymentStatus(val description: String) {
    SUCCESS("성공"),
    FAILED("실패"),
    CANCELED("취소"),
}
