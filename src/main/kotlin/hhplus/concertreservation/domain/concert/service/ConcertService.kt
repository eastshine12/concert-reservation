package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.ConcertNotFoundException
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.exception.SeatsNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertManager: ConcertManager,
    private val concertRepository: ConcertRepository,
    private val concertScheduleRepository: ConcertScheduleRepository,
    private val seatRepository: SeatRepository,
) {
    fun getConcertById(concertId: Long) : Concert {
        return concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException("Concert with id $concertId not found")
    }

    fun getScheduleById(scheduleId: Long) : ConcertSchedule {
        return concertManager.getScheduleById(scheduleId)
    }

    fun getSchedulesByConcertId(concertId: Long): List<ConcertSchedule> {
        return concertScheduleRepository.findAllByConcertId(concertId).takeIf { it.isNotEmpty() }
            ?: throw ConcertScheduleNotFoundException("No schedules found for the concert: $concertId")
    }

    fun getSeatById(seatId: Long): Seat {
        return seatRepository.findByIdOrNull(seatId)
            ?: throw SeatsNotFoundException("Seat with id $seatId not found")
    }
    fun getSeatsByScheduleId(scheduleId: Long): List<Seat> {
        return seatRepository.findAllByScheduleId(scheduleId).takeIf { it.isNotEmpty() }
            ?: throw SeatsNotFoundException("No seats found for the schedule: $scheduleId")
    }
}
