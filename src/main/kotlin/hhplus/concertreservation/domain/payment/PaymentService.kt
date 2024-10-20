package hhplus.concertreservation.domain.payment

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
) {
    fun savePayment(
        userId: Long,
        reservationId: Long,
        amount: BigDecimal,
    ): Payment {
        val payment = Payment.create(userId, reservationId, amount)
        return paymentRepository.save(payment)
    }

    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findAllByUserId(userId)
    }
}
