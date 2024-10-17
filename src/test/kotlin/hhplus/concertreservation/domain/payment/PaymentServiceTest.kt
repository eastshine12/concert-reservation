package hhplus.concertreservation.domain.payment

import hhplus.concertreservation.domain.common.enums.PaymentStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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

        every { paymentRepository.save(any()) } returns payment

        // when
        val result = paymentService.savePayment(userId, reservationId, amount)

        // then
        assertEquals(payment, result)
    }
}
