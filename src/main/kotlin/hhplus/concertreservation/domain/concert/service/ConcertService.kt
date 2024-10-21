package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.exception.ConcertNotFoundException
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.exception.SeatAvailabilityException
import hhplus.concertreservation.domain.concert.exception.SeatsNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import hhplus.concertreservation.domain.concert.toSeatInfo
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertManager: ConcertManager,
    private val concertRepository: ConcertRepository,
    private val concertScheduleRepository: ConcertScheduleRepository,
    private val seatRepository: SeatRepository,
) {
    fun getConcertById(concertId: Long): Concert {
        return concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException("Concert not found for id: $concertId")
    }

    fun getScheduleById(scheduleId: Long): ConcertSchedule {
        return concertManager.getScheduleById(scheduleId)
    }

    fun getSchedulesByConcertId(concertId: Long): List<ConcertSchedule> {
        return concertScheduleRepository.findAllByConcertId(concertId).takeIf { it.isNotEmpty() }
            ?: throw ConcertScheduleNotFoundException("No schedules found for the concert: $concertId")
    }

    fun verifyAndGetSeatInfo(seatId: Long): SeatInfo {
        val seat: Seat =
            seatRepository.findByIdOrNull(seatId)
                ?: throw SeatsNotFoundException("Seat not found for id: $seatId")
        if (seat.status == SeatStatus.AVAILABLE) {
            throw SeatAvailabilityException("The seat is not reserved with id ${seat.id}")
        }
        return seat.toSeatInfo()
    }

    fun getSeatsByScheduleId(scheduleId: Long): List<Seat> {
        return seatRepository.findAllByScheduleId(scheduleId).takeIf { it.isNotEmpty() }
            ?: throw SeatsNotFoundException("No seats found for the schedule: $scheduleId")
    }
}
