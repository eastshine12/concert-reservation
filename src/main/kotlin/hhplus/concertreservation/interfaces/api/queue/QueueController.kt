package hhplus.concertreservation.interfaces.api.queue

import hhplus.concertreservation.interfaces.api.queue.dto.res.QueueResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/queue")
class QueueController {

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