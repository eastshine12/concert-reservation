package hhplus.concertreservation.interfaces.api.concert.dto.res

import hhplus.concertreservation.domain.concert.dto.info.CreateReservationInfo

data class ReserveSeatResponse(
    val reservationId: Long,
    val status: String,
) {
    companion object {
        fun fromInfo(reservationInfo: CreateReservationInfo): ReserveSeatResponse {
            return ReserveSeatResponse(
                reservationId = reservationInfo.reservationId,
                status = if (reservationInfo.success) "success" else "failure",
            )
        }
    }
}
