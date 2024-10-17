package hhplus.concertreservation.domain.concert.repository

import hhplus.concertreservation.domain.concert.entity.Concert

interface ConcertRepository {
    fun findByIdOrNull(id: Long): Concert?
}
