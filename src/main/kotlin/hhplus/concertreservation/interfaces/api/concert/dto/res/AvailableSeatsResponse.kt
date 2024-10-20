package hhplus.concertreservation.interfaces.api.concert.dto.res

import hhplus.concertreservation.domain.concert.dto.info.SeatInfo

data class AvailableSeatsResponse(
    val seats: List<SeatResponse>,
) {
    companion object {
        fun fromInfoList(seatInfoList: List<SeatInfo>): AvailableSeatsResponse {
            return AvailableSeatsResponse(
                seats =
                    seatInfoList.map { seat ->
                        SeatResponse(
                            seatNumber = seat.seatNumber,
                            status = seat.status,
                        )
                    },
            )
        }
    }
}

data class SeatResponse(
    val seatNumber: Int,
    val status: String,
)
