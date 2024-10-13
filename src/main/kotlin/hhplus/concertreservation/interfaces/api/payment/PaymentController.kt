package hhplus.concertreservation.interfaces.api.payment

import hhplus.concertreservation.interfaces.api.payment.dto.req.PaymentRequest
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentResponse
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentDetail
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentHistoryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController {

    @PostMapping
    fun processPayment(
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val response = PaymentResponse(
            paymentId = 1L,
            amount = 70_000,
            status = "success",
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping()
    fun getPaymentHistory(
        @RequestParam userId: Int
    ): ResponseEntity<PaymentHistoryResponse> {
        val payments = listOf(
            PaymentDetail(id = 3, title = "BTS 월드 투어", time = "2024-10-05T19:00:00", seatNumber = "A1", price = 110000, status = "success"),
            PaymentDetail(id = 4, title = "IU 상암 콘서트", time = "2024-10-10T19:00:00", seatNumber = "B16", price = 90000, status = "success")
        )
        val response = PaymentHistoryResponse(payments = payments)
        return ResponseEntity.ok(response)
    }
}
