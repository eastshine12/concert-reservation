package hhplus.concertreservation.interfaces.api.payment

import hhplus.concertreservation.interfaces.api.payment.dto.req.PaymentRequest
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payment")
class PaymentController {

    @PostMapping
    fun processPayment(
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val response = PaymentResponse(status = "success")
        return ResponseEntity.ok(response)
    }
}
