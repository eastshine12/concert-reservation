package hhplus.concertreservation.interfaces.api.waitingQueue

import hhplus.concertreservation.interfaces.api.waitingQueue.dto.req.TokenRequest
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.QueueResponse
import hhplus.concertreservation.interfaces.api.waitingQueue.dto.res.TokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "대기열 API", description = "대기열 토큰 발급 및 대기 상태 조회 API")
interface IWaitingQueueController {
    @Operation(summary = "대기열 토큰 발급", description = "대기열에서 사용할 토큰을 발급합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "대기열 토큰 발급이 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                                    "status": "issued"
                                }
                            """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 값 (필수 필드 누락 또는 형식 오류)",
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
                            """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "유저 또는 콘서트를 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "User(Concert) not found"
                                }
                            """,
                            ),
                        ],
                    ),
                ],
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
                            """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun issueToken(
        @Schema(
            description = "대기열 토큰을 발급할 정보를 포함한 요청 객체",
            example = """
        {
            "concertId": 2,
            "scheduleId": 3,
            "userId": 123
        }
        """,
        )
        @RequestBody request: TokenRequest,
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        )
        @RequestHeader("Queue-Token", required = false) token: String?,
    ): ResponseEntity<TokenResponse>

    @Operation(summary = "대기열 상태 조회", description = "대기열에 있는 유저의 대기 상태를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "대기열 조회가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "PENDING",
                                    "remainingPosition": 10
                                }
                            """,
                            ),
                        ],
                    ),
                ],
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
                            """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "대기열 토큰을 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "Token not found"
                                }
                            """,
                            ),
                        ],
                    ),
                ],
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
                            """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getQueueStatus(
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        )
        @RequestHeader("Queue-Token") token: String,
    ): ResponseEntity<QueueResponse>
}
