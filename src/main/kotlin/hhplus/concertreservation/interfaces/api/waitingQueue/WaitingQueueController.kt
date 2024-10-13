package hhplus.concertreservation.interfaces.api.waitingQueue

import hhplus.concertreservation.interfaces.api.waitingQueue.dto.req.TokenRequest
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.TokenResponse
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.QueueResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/waiting-queues")
class WaitingQueueController {

    @PostMapping
    fun issueToken(
        @RequestBody request: TokenRequest,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            TokenResponse(
                token= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                status = "issued",
            )
        )
    }

    @GetMapping
    fun getQueueStatus(
        @RequestHeader("Queue-Token") token: String,
    ): ResponseEntity<QueueResponse> {
        return ResponseEntity.ok(
            QueueResponse(
                status = "waiting",
                queuePosition = "10",
            )
        )
    }
}
