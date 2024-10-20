package hhplus.concertreservation.application.concert

import hhplus.concertreservation.domain.concert.dto.command.ReservationCommand
import hhplus.concertreservation.domain.concert.dto.info.ConcertInfo
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.concert.service.ReservationService
import hhplus.concertreservation.domain.concert.toConcertInfo
import hhplus.concertreservation.domain.concert.toSeatInfo
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component

@Component
class ConcertFacade(
    private val waitingQueueService: WaitingQueueService,
    private val userService: UserService,
    private val concertService: ConcertService,
    private val reservationService: ReservationService,
) {
    fun getReservationAvailableDates(
        token: String,
        concertId: Long,
    ): ConcertInfo {
        waitingQueueService.validateTokenState(token)
        val concert: Concert = concertService.getConcertById(concertId)
        val concertSchedules: List<ConcertSchedule> = concertService.getSchedulesByConcertId(concertId)
        return concert.toConcertInfo(concertSchedules)
    }

    fun getSeatsInfo(
        token: String,
        scheduleId: Long,
    ): List<SeatInfo> {
        waitingQueueService.validateTokenState(token, scheduleId)
        val schedule: ConcertSchedule = concertService.getScheduleById(scheduleId)
        val seats: List<Seat> = concertService.getSeatsByScheduleId(schedule.id)
        return seats.map { it.toSeatInfo() }
    }

    fun createReservation(command: ReservationCommand): ReservationInfo {
        waitingQueueService.validateTokenState(command.token, command.scheduleId)
        val user: User = userService.getByUserId(command.userId)
        val schedule: ConcertSchedule = concertService.getScheduleById(command.scheduleId)
        return reservationService.createPendingReservation(user.id, schedule.id, command.seatId)
    }
}
