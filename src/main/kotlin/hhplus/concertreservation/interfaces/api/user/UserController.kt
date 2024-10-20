package hhplus.concertreservation.interfaces.api.user

import hhplus.concertreservation.application.user.UserFacade
import hhplus.concertreservation.interfaces.api.user.dto.req.ChargeBalanceRequest
import hhplus.concertreservation.interfaces.api.user.dto.res.BalanceResponse
import hhplus.concertreservation.interfaces.api.user.dto.res.ChargeBalanceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userFacade: UserFacade,
) : IUserController {
    @PatchMapping("/{userId}/balance")
    override fun chargeBalance(
        @PathVariable userId: Long,
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: ChargeBalanceRequest,
    ): ResponseEntity<ChargeBalanceResponse> {
        return ResponseEntity.ok(
            ChargeBalanceResponse.fromInfo(userFacade.chargeBalance(request.toCommand(token, userId))),
        )
    }

    @GetMapping("/{userId}/balance")
    override fun getBalance(
        @PathVariable userId: Long,
        @RequestHeader("Queue-Token") token: String,
    ): ResponseEntity<BalanceResponse> {
        return ResponseEntity.ok(
            BalanceResponse.fromInfo(userFacade.getUserBalance(token, userId)),
        )
    }
}
