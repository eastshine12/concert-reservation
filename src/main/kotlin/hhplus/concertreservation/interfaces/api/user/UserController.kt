package hhplus.concertreservation.interfaces.api.user

import hhplus.concertreservation.interfaces.api.user.dto.req.ChargeBalanceRequest
import hhplus.concertreservation.interfaces.api.user.dto.res.BalanceResponse
import hhplus.concertreservation.interfaces.api.user.dto.res.ChargeBalanceResponse
import hhplus.concertreservation.interfaces.api.user.dto.res.PaymentDetail
import hhplus.concertreservation.interfaces.api.user.dto.res.PaymentHistoryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController {

    @PostMapping("/{userId}/charge")
    fun chargeBalance(
        @PathVariable userId: Int,
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: ChargeBalanceRequest
    ): ResponseEntity<ChargeBalanceResponse> {
        return ResponseEntity.ok(
            ChargeBalanceResponse(status = "success")
        )
    }

    @GetMapping("/{userId}/balance")
    fun getBalance(
        @PathVariable userId: Int,
        @RequestHeader("Queue-Token") token: String
    ): ResponseEntity<BalanceResponse> {
        return ResponseEntity.ok(
            BalanceResponse(balance = 50000)
        )
    }

    @GetMapping("/{userId}/payments")
    fun getPaymentHistory(
        @PathVariable userId: Int
    ): ResponseEntity<PaymentHistoryResponse> {
        val payments = listOf(
            PaymentDetail(id = 3, title = "BTS 월드 투어", time = "2024-10-05T19:00:00", seatNumber = "A1", price = 110000, status = "success"),
            PaymentDetail(id = 4, title = "IU 상암 콘서트", time = "2024-10-10T19:00:00", seatNumber = "B16", price = 90000, status = "success")
        )
        val response = PaymentHistoryResponse(payments = payments)
        return ResponseEntity.ok(response)
    }
}
