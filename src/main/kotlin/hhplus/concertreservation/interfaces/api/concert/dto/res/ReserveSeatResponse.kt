package hhplus.concertreservation.interfaces.api.concert.dto.res

import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo

data class ReserveSeatResponse(
    val status: String,
) {
    companion object {
        fun fromInfo(reservationInfo: ReservationInfo): ReserveSeatResponse {
            return ReserveSeatResponse(
                status = if (reservationInfo.success) "success" else "failure",
            )
        }
    }
}
