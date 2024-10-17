package hhplus.concertreservation.infrastructure.repository.user

import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryJpaImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun findByIdOrNull(id: Long): User? {
        return userJpaRepository.findByIdOrNull(id)
    }
}
