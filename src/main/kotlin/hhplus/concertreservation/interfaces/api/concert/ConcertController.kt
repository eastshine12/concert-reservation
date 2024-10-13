package hhplus.concertreservation.interfaces.api.concert

import hhplus.concertreservation.interfaces.api.concert.dto.req.ReserveSeatRequest
import hhplus.concertreservation.interfaces.api.concert.dto.res.*
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Schedules
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/concerts")
class ConcertController {

    @GetMapping("/{concertId}")
    fun getAvailableDates(
        @RequestHeader("Queue-Token") token: String,
        @PathVariable concertId: Int,
    ): ResponseEntity<ConcertResponse> {
        return ResponseEntity.ok(
            ConcertResponse(
                concertId = 1L,
                title = "BTS 월드 투어",
                schedules = listOf(
                    SchedulesResponse(
                        scheduleId = 1L,
                        startTime = "2024-10-01 19:00:00",
                        totalSeats = 50,
                        availableSeats = 5,
                    ),
                    SchedulesResponse(
                        scheduleId = 2L,
                        startTime = "2024-10-02 19:00:00",
                        totalSeats = 50,
                        availableSeats = 10,
                    ),
                )
            )
        )
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    fun getAvailableSeats(
        @RequestHeader("Queue-Token") token: String,
        @PathVariable concertId: Int,
        @PathVariable scheduleId: Int,
    ): ResponseEntity<AvailableSeatsResponse> {
        val availableSeats = listOf(
            SeatResponse(seatNumber = "A1", status = "available"),
            SeatResponse(seatNumber = "A2", status = "available"),
            SeatResponse(seatNumber = "A3", status = "reserved"),
        )
        return ResponseEntity.ok(AvailableSeatsResponse(seats = availableSeats))
    }

    @PostMapping("/reservations")
    fun reserveSeat(
        @RequestHeader("Queue-Token") token: String,
        @RequestBody request: ReserveSeatRequest,
    ): ResponseEntity<ReserveSeatResponse> {
        return ResponseEntity.ok(
            ReserveSeatResponse(status = "success")
        )
    }
}
