package hhplus.concertreservation.application.concert

import hhplus.concertreservation.application.concert.dto.command.ReservationCommand
import hhplus.concertreservation.application.concert.dto.info.ConcertInfo
import hhplus.concertreservation.application.concert.dto.info.ReservationInfo
import hhplus.concertreservation.application.concert.dto.info.SeatInfo
import hhplus.concertreservation.application.concert.mapper.ConcertMapper
import hhplus.concertreservation.application.concert.mapper.SeatMapper
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.service.ConcertScheduleService
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.concert.service.ReservationService
import hhplus.concertreservation.domain.concert.service.SeatService
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.service.UserService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component

@Component
class ConcertFacade(
    private val waitingQueueService: WaitingQueueService,
    private val userService: UserService,
    private val concertService: ConcertService,
    private val scheduleService: ConcertScheduleService,
    private val seatService: SeatService,
    private val reservationService: ReservationService,
    private val concertMapper: ConcertMapper,
    private val seatMapper: SeatMapper,
) {

    fun getReservationAvailableDates(token: String, concertId: Long): ConcertInfo {
        waitingQueueService.validateTokenState(token)
        val concert: Concert = concertService.getConcertById(concertId)
        val concertSchedules: List<ConcertSchedule> = scheduleService.getSchedulesByConcertId(concertId)
        return concertMapper.toConcertInfo(concert, concertSchedules)
    }

    fun getSeatsInfo(token: String, scheduleId: Long): List<SeatInfo> {
        waitingQueueService.validateTokenState(token, scheduleId)
        val schedule: ConcertSchedule = scheduleService.getScheduleById(scheduleId)
        val seats: List<Seat> = seatService.getSeatsByScheduleId(schedule.id)
        return seats.map { seatMapper.toSeatInfo(it) }
    }

    fun createReservation(command: ReservationCommand): ReservationInfo {
        waitingQueueService.validateTokenState(command.token, command.scheduleId)
        val user: User = userService.getByUserId(command.userId)
        val schedule: ConcertSchedule = scheduleService.getScheduleById(command.scheduleId)
        val reservation: Reservation = reservationService.createPendingReservation(user.id, schedule.id, command.seatId)
        return concertMapper.toReservationInfo(reservation)
    }
}
