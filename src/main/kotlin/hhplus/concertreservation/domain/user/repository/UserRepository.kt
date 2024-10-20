package hhplus.concertreservation.domain.user.repository

import hhplus.concertreservation.domain.user.entity.User

interface UserRepository {
    fun save(user: User): User

    fun findByIdOrNull(id: Long): User?
}
