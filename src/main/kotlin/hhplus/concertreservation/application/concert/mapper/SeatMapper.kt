package hhplus.concertreservation.application.concert.mapper

import hhplus.concertreservation.application.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Seat
import org.springframework.stereotype.Component

@Component
class SeatMapper {
    fun toSeatInfo(seat: Seat): SeatInfo {
        return SeatInfo(
            seatNumber = seat.seatNumber,
            status = seat.status.name,
            price = seat.price
        )
    }
}
