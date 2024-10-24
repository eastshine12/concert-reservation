package hhplus.concertreservation.interfaces.api.concert

import hhplus.concertreservation.interfaces.api.concert.dto.req.ReserveSeatRequest
import hhplus.concertreservation.interfaces.api.concert.dto.res.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "콘서트 API", description = "콘서트 예약 및 좌석 조회 API")
interface IConcertController {
    @Operation(summary = "예약 가능한 콘서트 날짜 조회", description = "예약 가능한 콘서트 날짜 및 남은 좌석 수를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "콘서트 조회가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "concertId": 1,
                                    "title": "BTS 월드 투어",
                                    "schedules": [
                                        {
                                            "scheduleId": 1,
                                            "startTime": "2024-10-01 19:00:00",
                                            "totalSeats": 50,
                                            "availableSeats": 5
                                        },
                                        {
                                            "scheduleId": 2,
                                            "startTime": "2024-10-02 19:00:00",
                                            "totalSeats": 50,
                                            "availableSeats": 10
                                        }
                                    ]
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 값 (유효하지 않은 토큰 형식)",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Invalid token format"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "콘서트를 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Concert not found"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Internal server error"
                                }
                            """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun getAvailableDates(
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        )
        @RequestHeader("Queue-Token") token: String,
        @Parameter(
            description = "예약 가능한 콘서트를 조회할 콘서트 ID",
            example = "123"
        )
        @PathVariable concertId: Long,
    ): ResponseEntity<ConcertResponse>

    @Operation(summary = "예약 가능한 좌석 조회", description = "특정 콘서트 스케줄의 예약 가능한 좌석을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "좌석 조회가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "seats": [
                                        {"seatNumber": 1, "status": "available"},
                                        {"seatNumber": 2, "status": "available"},
                                        {"seatNumber": 3, "status": "reserved"}
                                    ]
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 값 (유효하지 않은 토큰 형식)",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Invalid token format"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "콘서트 또는 콘서트 스케줄을 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Concert (schedule) not found"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Internal server error"
                                }
                            """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun getAvailableSeats(
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        )
        @RequestHeader("Queue-Token") token: String,
        @Parameter(
            description = "예약 가능한 좌석 목록을 조회할 콘서트 ID",
            example = "123"
        )
        @PathVariable concertId: Int,
        @Parameter(
            description = "예약 가능한 좌석 목록을 조회할 콘서트 스케줄 ID",
            example = "123"
        )
        @PathVariable scheduleId: Long,
    ): ResponseEntity<AvailableSeatsResponse>

    @Operation(summary = "좌석 예약", description = "특정 콘서트 스케줄에 좌석을 예약합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "좌석 예약이 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "success"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 값 (유효하지 않은 토큰 형식)",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Invalid token format"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "콘서트 또는 콘서트 스케줄 또는 좌석을 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Concert (schedule/seat) not found"
                                }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Internal server error"
                                }
                            """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun reserveSeat(
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        )
        @RequestHeader("Queue-Token") token: String,
        @Schema(
            description = "특정 콘서트 스케줄에 예약할 정보를 포함한 요청 객체",
            example = """
        {
            "userId": 123,
            "concertId": 2,
            "scheduleId": 3,
            "seatId": 5
        }
        """
        )
        @RequestBody request: ReserveSeatRequest,
    ): ResponseEntity<ReserveSeatResponse>
}
