package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import hhplus.concertreservation.domain.concert.toSeatInfo
import org.springframework.cache.annotation.Cacheable
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
            ?: throw CoreException(
                ErrorType.NO_CONCERT_FOUND,
                details = mapOf("concertId" to concertId),
            )
    }

    fun checkScheduleExists(scheduleId: Long) {
        concertManager.getScheduleById(scheduleId)
    }

    fun checkScheduleAvailability(scheduleId: Long) {
        if (concertManager.getScheduleById(scheduleId).availableSeats == 0) {
            throw CoreException(
                errorType = ErrorType.CONCERT_SCHEDULE_SOLD_OUT,
                details = mapOf("scheduleId" to scheduleId),
            )
        }
    }

    @Cacheable(value = ["concertSchedules"], key = "#concertId")
    fun getSchedulesByConcertId(concertId: Long): List<ConcertSchedule> {
        return concertScheduleRepository.findAllByConcertId(concertId).takeIf { it.isNotEmpty() }
            ?: throw CoreException(
                errorType = ErrorType.NO_CONCERT_SCHEDULE_FOUND,
                details =
                    mapOf(
                        "concertId" to concertId,
                    ),
            )
    }

    fun verifyAndGetSeatInfo(seatId: Long): SeatInfo {
        val seat: Seat =
            seatRepository.findByIdOrNull(seatId)
                ?: throw CoreException(
                    errorType = ErrorType.NO_SEAT_FOUND,
                    details =
                        mapOf(
                            "seatId" to seatId,
                        ),
                )
        if (seat.status == SeatStatus.AVAILABLE) {
            throw CoreException(
                errorType = ErrorType.SEAT_UNAVAILABLE,
                details =
                    mapOf(
                        "seatId" to seatId,
                    ),
            )
        }
        return seat.toSeatInfo()
    }

    fun getSeatsByScheduleId(scheduleId: Long): List<Seat> {
        return seatRepository.findAllByScheduleId(scheduleId).takeIf { it.isNotEmpty() }
            ?: throw CoreException(
                errorType = ErrorType.NO_SEAT_FOUND,
                message = "No seats found for the given schedule.",
                details =
                    mapOf(
                        "scheduleId" to scheduleId,
                    ),
            )
    }
}
