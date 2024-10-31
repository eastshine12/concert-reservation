package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class PaymentServiceTest {
    private val paymentRepository = mockk<PaymentRepository>()
    private val paymentService = PaymentService(paymentRepository)

    @Test
    fun `should save payment successfully`() {
        // given
        val userId = 1L
        val reservationId = 1L
        val amount = BigDecimal("100.00")
        val payment = Payment(userId, reservationId, amount, PaymentStatus.SUCCESS, 1L)
        val paymentInfo = payment.toPaymentInfo()

        every { paymentRepository.findAllByUserId(userId) } returns emptyList()
        every { paymentRepository.save(any()) } returns payment

        // when
        val result = paymentService.savePayment(userId, reservationId, amount)

        // then
        assertEquals(paymentInfo, result)
    }

    @Test
    fun `should throw exception if payment already processed`() {
        // given
        val userId = 1L
        val reservationId = 1L
        val amount = BigDecimal("100.00")
        val successfulPayment =
            Payment(userId, reservationId, amount, PaymentStatus.SUCCESS, 1L)

        every { paymentRepository.findAllByUserId(userId) } returns listOf(successfulPayment)

        // when & then
        val exception =
            assertThrows<CoreException> {
                paymentService.savePayment(userId, reservationId, amount)
            }

        assertEquals(ErrorType.PAYMENT_ALREADY_PROCESSED, exception.errorType)
    }
}
