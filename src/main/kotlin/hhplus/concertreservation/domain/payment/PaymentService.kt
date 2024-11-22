package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun savePaymentHistory(
        userId: Long,
        reservationId: Long,
        amount: BigDecimal,
    ): PaymentInfo {
        val successfulPayment =
            paymentRepository.findAllByUserId(userId)
                .firstOrNull { it.reservationId == reservationId && it.status == PaymentStatus.SUCCESS }
        if (successfulPayment != null) {
            throw CoreException(
                errorType = ErrorType.PAYMENT_ALREADY_PROCESSED,
                details =
                    mapOf(
                        "reservationId" to reservationId,
                        "paymentId" to successfulPayment.id,
                    ),
            )
        }
        val payment = Payment.create(userId, reservationId, amount)
        return paymentRepository.save(payment).toPaymentInfo()
    }

    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findAllByUserId(userId)
    }
}
