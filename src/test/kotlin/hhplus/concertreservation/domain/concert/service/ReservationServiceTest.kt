package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.dto.info.CreateReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.toCreateReservationInfo
import hhplus.concertreservation.domain.concert.toReservationInfo
import io.mockk.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
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
        val createReservationInfo = mockk<CreateReservationInfo>(relaxed = true)

        every { seatFinder.getAvailableSeatWithLock(scheduleId, seatId) } returns seat
        every { concertManager.createPendingReservation(userId, scheduleId, seatId) } returns reservation
        every { concertManager.getScheduleById(scheduleId) } returns mockk(relaxed = true)
        every { reservation.toCreateReservationInfo(success = true) } returns createReservationInfo

        // when
        val result = reservationService.createPendingReservation(userId, scheduleId, seatId)

        // then
        assertEquals(createReservationInfo, result)
    }

    @Test
    fun `should confirm reservation successfully`() {
        // given
        val reservationId = 1L
        val reservation =
            spyk(
                Reservation(
                    id = reservationId,
                    userId = 1L,
                    scheduleId = 1L,
                    seatId = 1L,
                    status = ReservationStatus.PENDING,
                    expiresAt = LocalDateTime.now().plusDays(1),
                ),
            )

        val expectedReservationInfo =
            ReservationInfo(
                reservationId = reservation.id,
                userId = reservation.userId,
                scheduleId = reservation.scheduleId,
                seatId = reservation.seatId,
                status = ReservationStatus.CONFIRMED.name,
                expiresAt = reservation.expiresAt,
            )

        every { concertManager.getReservationWithLock(reservationId) } returns reservation
        every { reservationRepository.save(reservation) } returns reservation
        every { reservation.toReservationInfo() } returns expectedReservationInfo

        // when
        val result = reservationService.confirmReservation(reservationId)

        // then
        assertEquals(expectedReservationInfo, result)
    }
}
