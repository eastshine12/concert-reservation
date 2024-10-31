package hhplus.concertreservation.application.concert

import hhplus.concertreservation.domain.concert.dto.command.ReservationCommand
import hhplus.concertreservation.domain.concert.dto.info.ConcertInfo
import hhplus.concertreservation.domain.concert.dto.info.CreateReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.concert.service.ReservationService
import hhplus.concertreservation.domain.concert.toConcertInfo
import hhplus.concertreservation.domain.concert.toSeatInfo
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component

@Component
class ConcertFacade(
    private val userService: UserService,
    private val concertService: ConcertService,
    private val reservationService: ReservationService,
    private val waitingQueueService: WaitingQueueService,
) {
    fun getReservationAvailableDates(
        token: String,
        concertId: Long,
    ): ConcertInfo {
        val concert: Concert = concertService.getConcertById(concertId)
        val concertSchedules: List<ConcertSchedule> = concertService.getSchedulesByConcertId(concertId)
        return concert.toConcertInfo(concertSchedules)
    }

    fun getSeatsInfo(
        token: String,
        scheduleId: Long,
    ): List<SeatInfo> {
        concertService.checkScheduleExists(scheduleId)
        val seats: List<Seat> = concertService.getSeatsByScheduleId(scheduleId)
        return seats.map { it.toSeatInfo() }
    }

    fun createReservation(command: ReservationCommand): CreateReservationInfo {
        waitingQueueService.verifyMatchingScheduleId(command.token, command.scheduleId)
        userService.checkUserExists(command.userId)
        concertService.checkScheduleAvailability(command.scheduleId)
        return reservationService.createPendingReservation(command.userId, command.scheduleId, command.seatId)
    }
}
