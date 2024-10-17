package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.enums.PaymentStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    fun savePayment(userId: Long, reservationId: Long, amount: BigDecimal): Payment {
        val payment = Payment(
            userId = userId,
            reservationId = reservationId,
            amount = amount,
            status = PaymentStatus.SUCCESS
        )

        return paymentRepository.save(payment)
    }

    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findAllByUserId(userId)
    }
}
