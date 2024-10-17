package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.exception.ConcertNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConcertServiceTest {

    private val concertRepository = mockk<ConcertRepository>()
    private val concertService = ConcertService(concertRepository)

    @Test
    fun `must throw exception when concert is not found`() {
        // given
        val concertId = 1L
        every { concertRepository.findByIdOrNull(concertId) } returns null

        // when, then
        assertThrows<ConcertNotFoundException> {
            concertService.getConcertById(concertId)
        }
    }
}
