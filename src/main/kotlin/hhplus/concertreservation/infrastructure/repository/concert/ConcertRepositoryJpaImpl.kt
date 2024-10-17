package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.repository.ConcertRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ConcertRepositoryJpaImpl(
    private val concertJpaRepository: ConcertJpaRepository
) : ConcertRepository {
    override fun findByIdOrNull(id: Long): Concert? {
        return concertJpaRepository.findByIdOrNull(id)
    }
}
