package hhplus.concertreservation.interfaces.api.token

import hhplus.concertreservation.interfaces.api.token.dto.req.TokenRequest
import hhplus.concertreservation.interfaces.api.token.dto.res.TokenResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/token")
class TokenController {

    @PostMapping
    fun issueToken(
        @RequestBody request: TokenRequest,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            TokenResponse(
                token= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                status = "issued",
            )
        )
    }
}