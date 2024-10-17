package hhplus.concertreservation.domain.common.enums

enum class QueueStatus(val description: String) {
    PENDING("대기"),
    ACTIVE("활성"),
    EXPIRED("만료"),
}
