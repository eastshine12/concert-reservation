package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Component

@Component
class SeatFinder(
    private val seatRepository: SeatRepository,
) {
    fun getSeatWithLock(seatId: Long): Seat {
        return seatRepository.findByIdOrNullWithLock(seatId)
            ?: throw CoreException(
                errorType = ErrorType.NO_SEAT_FOUND,
                details = mapOf(
                    "seatId" to seatId,
                ),
            )
    }

    fun getAvailableSeatWithLock(
        scheduleId: Long,
        seatId: Long,
    ): Seat {
        val seat = getSeatWithLock(seatId)

        if (seat.scheduleId != scheduleId) {
            throw CoreException(
                errorType = ErrorType.NO_SEAT_FOUND,
                message = "Seat does not belong to concert schedule.",
                details = mapOf(
                    "seatId" to seatId,
                    "scheduleId" to scheduleId,
                ),
            )
        }

        if (seat.status != SeatStatus.AVAILABLE) {
            throw CoreException(
                errorType = ErrorType.SEAT_UNAVAILABLE,
                details = mapOf(
                    "seatId" to seatId,
                ),
            )
        }

        return seat
    }
}
