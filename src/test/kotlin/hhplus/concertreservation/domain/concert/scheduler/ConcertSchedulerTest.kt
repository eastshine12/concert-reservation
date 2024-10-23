package hhplus.concertreservation.domain.concert.scheduler

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConcertSchedulerTest {
    private val reservationRepository = mockk<ReservationRepository>()
    private val scheduleRepository = mockk<ConcertScheduleRepository>()
    private val seatRepository = mockk<SeatRepository>()
    private val concertManager = mockk<ConcertManager>()
    private val seatFinder = mockk<SeatFinder>()
    private val concertScheduler =
        ConcertScheduler(reservationRepository, scheduleRepository, seatRepository, concertManager, seatFinder)

    @Test
    fun `must throw exception when failing to cancel expired reservation`() {
        // given
        val expiredReservation = mockk<Reservation>()
        every { reservationRepository.findExpiredReservations(any()) } returns listOf(expiredReservation)
        every { expiredReservation.cancel() } throws IllegalStateException("Failed to cancel reservation")

        // when / then
        assertThrows<IllegalStateException> {
            concertScheduler.expireReservations()
        }
    }
}
