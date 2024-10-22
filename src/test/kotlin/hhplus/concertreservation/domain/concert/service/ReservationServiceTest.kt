package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.toCreateReservationInfo
import hhplus.concertreservation.domain.concert.toReservationInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReservationServiceTest {
    private val seatFinder = mockk<SeatFinder>()
    private val concertManager = mockk<ConcertManager>()
    private val reservationRepository = mockk<ReservationRepository>()
    private val reservationService = ReservationService(seatFinder, concertManager, reservationRepository)

    @Test
    fun `should create pending reservation successfully`() {
        // given
        val userId = 1L
        val scheduleId = 1L
        val seatId = 1L
        val seat = mockk<Seat>(relaxed = true)
        val reservation = mockk<Reservation>(relaxed = true)
        val createReservationInfo = reservation.toCreateReservationInfo(success = true)

        every { seatFinder.getAvailableSeatWithLock(scheduleId, seatId) } returns seat
        every { concertManager.getScheduleById(scheduleId) } returns mockk(relaxed = true)
        every { concertManager.createPendingReservation(userId, scheduleId, seatId) } returns reservation

        // when
        val result = reservationService.createPendingReservation(userId, scheduleId, seatId)

        // then
        assertEquals(createReservationInfo, result)
    }

    @Test
    fun `should confirm reservation successfully`() {
        // given
        val reservationId = 1L
        val reservation = mockk<Reservation>(relaxed = true)
        val reservationInfo = reservation.toReservationInfo()

        every { concertManager.getReservationWithLock(reservationId) } returns reservation
        every { reservationRepository.save(reservation) } returns reservation

        // when
        val result = reservationService.confirmReservation(reservationId)

        // then
        assertEquals(reservationInfo, result)
    }
}
