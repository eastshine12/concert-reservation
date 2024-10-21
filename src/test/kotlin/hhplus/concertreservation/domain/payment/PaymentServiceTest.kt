package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.enums.PaymentStatus
import hhplus.concertreservation.domain.payment.dto.info.PaymentInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
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
        val payment = Payment(userId, reservationId, amount, PaymentStatus.SUCCESS)
        val paymentInfo = mockk<PaymentInfo>(relaxed = true)

        every { Payment.create(userId, reservationId, amount) } returns payment
        every { paymentRepository.save(any()) } returns payment
        every { payment.toPaymentInfo() } returns paymentInfo

        // when
        val result = paymentService.savePayment(userId, reservationId, amount)

        // then
        assertEquals(paymentInfo, result)
    }
}
