package hhplus.concertreservation.interfaces.api.user

import hhplus.concertreservation.interfaces.api.user.dto.req.ChargeBalanceRequest
import hhplus.concertreservation.interfaces.api.user.dto.res.BalanceResponse
import hhplus.concertreservation.interfaces.api.user.dto.res.ChargeBalanceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController {

    @PatchMapping("/{userId}/balance")
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
}
