package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.SeatStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Seat(
    var scheduleId: Long,
    var seatNumber: Int,
    var price: BigDecimal,
    @Enumerated(EnumType.STRING)
    var status: SeatStatus,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {

    fun reserve() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw IllegalStateException("Seat is not available for reservation.")
        }
        this.status = SeatStatus.UNAVAILABLE
    }

    fun markAsAvailable() {
        if (status == SeatStatus.AVAILABLE) {
            throw IllegalStateException("Seat is already available.")
        }
        this.status = SeatStatus.AVAILABLE
    }
}
