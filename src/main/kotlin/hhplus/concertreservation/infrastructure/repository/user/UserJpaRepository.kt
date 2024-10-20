package hhplus.concertreservation.infrastructure.repository.user

import hhplus.concertreservation.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long>
