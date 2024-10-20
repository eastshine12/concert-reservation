package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.SeatStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Seat(
    val scheduleId: Long,
    seatNumber: Int,
    price: BigDecimal,
    status: SeatStatus,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    var seatNumber: Int = seatNumber
        protected set

    var price: BigDecimal = price
        protected set

    @Enumerated(EnumType.STRING)
    var status: SeatStatus = status
        protected set

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
