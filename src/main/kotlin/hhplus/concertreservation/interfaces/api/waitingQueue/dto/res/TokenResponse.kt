package hhplus.concertreservation.interfaces.api.waitingQueue.dto.res

import hhplus.concertreservation.application.waitingQueue.dto.info.TokenInfo

data class TokenResponse(
    val token: String,
    val status: String,
) {
    companion object {
        fun fromInfo(info: TokenInfo): TokenResponse {
            return TokenResponse(
                token = info.token,
                status = info.status.name
            )
        }
    }
}
