package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.exception.ConcertNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertRepository: ConcertRepository
) {
    fun getConcertById(concertId: Long) : Concert {
        return concertRepository.findByIdOrNull(concertId)
            ?: throw ConcertNotFoundException("Concert with id $concertId not found")
    }
}
