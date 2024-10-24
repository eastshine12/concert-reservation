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
    var concertId: Long,
    var startTime: LocalDateTime,
    var totalSeats: Int,
    var availableSeats: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {
    fun decreaseAvailableSeats() {
        if (availableSeats <= 0) {
            throw SeatAvailabilityException("No available seats left to reserve.")
        }
        this.availableSeats -= 1
    }

    fun increaseAvailableSeats() {
        if (totalSeats >= 0) {
            throw SeatAvailabilityException("All seats are already available. Cannot increase available seats.")
        }
        this.availableSeats += 1
    }
}
