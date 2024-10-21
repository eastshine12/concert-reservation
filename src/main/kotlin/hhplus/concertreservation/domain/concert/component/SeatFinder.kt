package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.SeatAvailabilityException
import hhplus.concertreservation.domain.concert.exception.SeatNotFoundException
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Component

@Component
class SeatFinder(
    private val seatRepository: SeatRepository,
) {
    fun getSeatWithLock(seatId: Long): Seat {
        return seatRepository.findByIdOrNullWithLock(seatId)
            ?: throw SeatNotFoundException("Seat not found with id $seatId")
    }

    fun getAvailableSeatWithLock(
        scheduleId: Long,
        seatId: Long,
    ): Seat {
        val seat =
            seatRepository.findByIdOrNullWithLock(seatId)
                ?: throw SeatNotFoundException("Seat not found with id $seatId ")

        if (seat.scheduleId != scheduleId) {
            throw SeatNotFoundException("Seat with id $seatId does not belong to schedule with id $scheduleId")
        }

        if (seat.status != SeatStatus.AVAILABLE) {
            throw SeatAvailabilityException("Seat is not available for reservation with id $seatId")
        }

        return seat
    }
}
