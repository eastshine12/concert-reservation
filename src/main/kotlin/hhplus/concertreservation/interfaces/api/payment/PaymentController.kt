package hhplus.concertreservation.interfaces.api.payment

import hhplus.concertreservation.application.payment.PaymentFacade
import hhplus.concertreservation.interfaces.api.payment.dto.req.PaymentRequest
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentResponse
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentDetail
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentHistoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentFacade: PaymentFacade,
) :IPaymentController {
    @PostMapping
    override fun processPayment(
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(
            PaymentResponse.fromInfo(paymentFacade.processPayment(request.toCommand(token)))
        )
    }

    @GetMapping()
    override fun getPaymentHistory(
        @RequestParam userId: Long
    ): ResponseEntity<PaymentHistoryResponse> {
        return ResponseEntity.ok(
            PaymentHistoryResponse.fromInfoList(paymentFacade.getPaymentsByUserId(userId))
        )
    }
}
