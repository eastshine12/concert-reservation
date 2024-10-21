package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
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
    ): PaymentInfo {
        val payment = Payment.create(userId, reservationId, amount)
        return paymentRepository.save(payment).toPaymentInfo()
    }

    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findAllByUserId(userId)
    }
}
