package hhplus.concertreservation.infrastructure.repository.payment

import hhplus.concertreservation.domain.payment.Payment
import hhplus.concertreservation.domain.payment.PaymentRepository
import hhplus.concertreservation.domain.user.entity.User
import hhplus.concertreservation.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryJpaImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentRepository {
    override fun save(payment: Payment): Payment {
        return paymentJpaRepository.save(payment)
    }

    override fun findAllByUserId(userId: Long): List<Payment> {
        return paymentJpaRepository.findAllByUserId(userId)
    }
}
