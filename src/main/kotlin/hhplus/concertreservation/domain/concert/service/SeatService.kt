package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.SeatsNotFoundException
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Service

@Service
class SeatService(
    private val seatRepository: SeatRepository,
) {
    fun getSeatById(seatId: Long): Seat {
        return seatRepository.findByIdOrNull(seatId)
            ?: throw SeatsNotFoundException("Seat with id $seatId not found")
    }
    fun getSeatsByScheduleId(scheduleId: Long): List<Seat> {
        return seatRepository.findAllByScheduleId(scheduleId).takeIf { it.isNotEmpty() }
            ?: throw SeatsNotFoundException("No seats found for the schedule: $scheduleId")
    }
}
