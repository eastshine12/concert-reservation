package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import org.springframework.stereotype.Service

@Service
class ConcertScheduleService(
    private val concertManager: ConcertManager,
    private val concertScheduleRepository: ConcertScheduleRepository
) {
    fun getScheduleById(scheduleId: Long) : ConcertSchedule {
        return concertManager.getScheduleById(scheduleId)
    }

    fun getSchedulesByConcertId(concertId: Long): List<ConcertSchedule> {
        return concertScheduleRepository.findAllByConcertId(concertId).takeIf { it.isNotEmpty() }
            ?: throw ConcertScheduleNotFoundException("No schedules found for the concert: $concertId")
    }
}
