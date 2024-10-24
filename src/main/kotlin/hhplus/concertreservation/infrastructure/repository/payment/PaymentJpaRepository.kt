package hhplus.concertreservation.infrastructure.repository.payment

import hhplus.concertreservation.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<Payment, Long> {
    fun findAllByUserId(userId: Long): List<Payment>
}
