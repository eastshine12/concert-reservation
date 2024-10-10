package hhplus.concertreservation.interfaces.api.concert.dto.res

data class AvailableSeatsResponse(
    val seats: List<SeatResponse>
)

data class SeatResponse(
    val seatNumber: String,
    val status: String,
)
