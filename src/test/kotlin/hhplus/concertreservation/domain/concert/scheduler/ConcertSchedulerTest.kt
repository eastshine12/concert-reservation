package hhplus.concertreservation.domain.concert.scheduler

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConcertSchedulerTest {
    private val reservationRepository = mockk<ReservationRepository>()
    private val concertManager = mockk<ConcertManager>()
    private val seatFinder = mockk<SeatFinder>()
    private val concertScheduler = ConcertScheduler(reservationRepository, concertManager, seatFinder)

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
