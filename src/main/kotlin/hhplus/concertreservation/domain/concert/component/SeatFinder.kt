package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.SeatNotFoundException
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Component

@Component
class SeatFinder(
    private val seatRepository: SeatRepository,
) {
    fun getSeatWithLock(seatId: Long): Seat {
        return seatRepository.findByIdOrNullWithLock(seatId)
            ?: throw SeatNotFoundException("Seat with id $seatId not found")
    }

    fun getAvailableSeatWithLock(
        scheduleId: Long,
        seatId: Long,
    ): Seat {
        val seat =
            seatRepository.findByIdOrNullWithLock(seatId)
                ?: throw SeatNotFoundException("Seat with id $seatId not found")

        if (seat.scheduleId != scheduleId || seat.status != SeatStatus.AVAILABLE) {
            throw IllegalStateException("The seat is either unavailable or does not belong to the schedule.")
        }

        return seat
    }
}
