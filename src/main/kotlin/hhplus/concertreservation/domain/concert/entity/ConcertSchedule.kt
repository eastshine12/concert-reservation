package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.concert.exception.SeatAvailabilityException
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class ConcertSchedule(
    val concertId: Long,
    startTime: LocalDateTime,
    totalSeats: Int,
    availableSeats: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    var startTime: LocalDateTime = startTime
        protected set

    var totalSeats: Int = totalSeats
        protected set

    var availableSeats: Int = availableSeats
        protected set

    fun occupySeat() {
        if (availableSeats <= 0) {
            throw SeatAvailabilityException("No available seats left to reserve.")
        }
        this.availableSeats -= 1
    }

    fun restoreSeat() {
        if (availableSeats >= totalSeats) {
            throw SeatAvailabilityException("All seats are already available. Cannot increase available seats.")
        }
        this.availableSeats += 1
    }
}
