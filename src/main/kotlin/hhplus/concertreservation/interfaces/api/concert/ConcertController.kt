package hhplus.concertreservation.interfaces.api.concert

import hhplus.concertreservation.application.concert.ConcertFacade
import hhplus.concertreservation.interfaces.api.concert.dto.req.ReserveSeatRequest
import hhplus.concertreservation.interfaces.api.concert.dto.res.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/concerts")
class ConcertController(
    private val concertFacade: ConcertFacade,
) : IConcertController {
    @GetMapping("/{concertId}")
    override fun getAvailableDates(
        @RequestHeader("Queue-Token") token: String,
        @PathVariable concertId: Long,
    ): ResponseEntity<ConcertResponse> {
        return ResponseEntity.ok(
            ConcertResponse.fromInfo(concertFacade.getReservationAvailableDates(token, concertId))
        )
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    override fun getAvailableSeats(
        @RequestHeader("Queue-Token") token: String,
        @PathVariable concertId: Int,
        @PathVariable scheduleId: Long,
    ): ResponseEntity<AvailableSeatsResponse> {
        return ResponseEntity.ok(
            AvailableSeatsResponse.fromInfoList(concertFacade.getSeatsInfo(token, scheduleId))
        )
    }

    @PostMapping("/reservations")
    override fun reserveSeat(
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: ReserveSeatRequest,
    ): ResponseEntity<ReserveSeatResponse> {
        return ResponseEntity.ok(
            ReserveSeatResponse.fromInfo(concertFacade.createReservation(request.toCommand(token)))
        )
    }
}
