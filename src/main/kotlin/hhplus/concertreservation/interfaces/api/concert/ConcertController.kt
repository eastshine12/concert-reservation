package hhplus.concertreservation.interfaces.api.concert

import hhplus.concertreservation.interfaces.api.concert.dto.req.ReserveSeatRequest
import hhplus.concertreservation.interfaces.api.concert.dto.res.AvailableDatesResponse
import hhplus.concertreservation.interfaces.api.concert.dto.res.AvailableSeatsResponse
import hhplus.concertreservation.interfaces.api.concert.dto.res.ReserveSeatResponse
import hhplus.concertreservation.interfaces.api.concert.dto.res.SeatResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/concert")
class ConcertController {

    @GetMapping("/{concertId}/available")
    fun getAvailableDates(
        @PathVariable concertId: Int,
        @RequestHeader("Queue-Token") token: String
    ): ResponseEntity<AvailableDatesResponse> {
        val availableDates = listOf(
            "2024-10-05T19:00:00",
            "2024-10-06T19:00:00",
            "2024-10-07T19:00:00"
        )
        return ResponseEntity.ok(
            AvailableDatesResponse(dates = availableDates)
        )
    }

    @GetMapping("/{concertId}/schedule/{scheduleId}/available")
    fun getAvailableSeats(
        @PathVariable concertId: Int,
        @PathVariable scheduleId: Int,
        @RequestHeader("Queue-Token") token: String
    ): ResponseEntity<AvailableSeatsResponse> {
        val availableSeats = listOf(
            SeatResponse(seatNumber = "A1", status = "available"),
            SeatResponse(seatNumber = "A2", status = "available"),
            SeatResponse(seatNumber = "A3", status = "reserved"),
        )
        return ResponseEntity.ok(AvailableSeatsResponse(seats = availableSeats))
    }

    @PostMapping("/{concertId}/schedule/{scheduleId}/reserve")
    fun reserveSeat(
        @PathVariable concertId: Int,
        @PathVariable scheduleId: Int,
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<ReserveSeatResponse> {
        return ResponseEntity.ok(
            ReserveSeatResponse(status = "success")
        )
    }
}
