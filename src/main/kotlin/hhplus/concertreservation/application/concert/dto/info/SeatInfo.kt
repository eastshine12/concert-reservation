package hhplus.concertreservation.application.concert.dto.info

import java.math.BigDecimal

data class SeatInfo(
    val seatNumber: Int,
    val status: String,
    val price: BigDecimal,
)
