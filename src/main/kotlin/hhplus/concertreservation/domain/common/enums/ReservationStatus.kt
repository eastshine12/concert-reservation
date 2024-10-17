package hhplus.concertreservation.domain.common.enums

enum class ReservationStatus(val description: String) {
    PENDING("임시"),
    CONFIRMED("확정"),
    CANCELED("취소"),
}
