package hhplus.concertreservation.interfaces.api.waitingQueue

import hhplus.concertreservation.application.waitingQueue.WaitingQueueFacade
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.req.TokenRequest
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.TokenResponse
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.QueueResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/waiting-queues")
class WaitingQueueController(
    private val waitingQueueFacade: WaitingQueueFacade,
) : IWaitingQueueController {
    @PostMapping
    override fun issueToken(
        @RequestBody request: TokenRequest,
        @RequestHeader("Queue-Token", required = false) token: String?,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            TokenResponse.fromInfo(
                waitingQueueFacade.getWaitingQueueToken(request.toCommand(token))
            )
        )
    }

    @GetMapping
    override fun getQueueStatus(
        @RequestHeader("Queue-Token") token: String,
    ): ResponseEntity<QueueResponse> {
        return ResponseEntity.ok(
            QueueResponse.fromInfo(waitingQueueFacade.getWaitingQueueStatus(token))
        )
    }
}
