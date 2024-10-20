package hhplus.concertreservation.interfaces.api.user

import hhplus.concertreservation.interfaces.api.user.dto.req.ChargeBalanceRequest
import hhplus.concertreservation.interfaces.api.user.dto.res.BalanceResponse
import hhplus.concertreservation.interfaces.api.user.dto.res.ChargeBalanceResponse
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

@Tag(name = "유저 API", description = "유저 관련 API")
interface IUserController {
    @Operation(summary = "잔액 충전", description = "유저의 잔액을 충전합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "충전 요청이 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChargeBalanceResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "success"
                                }
                            """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 값 (유효하지 않은 토큰 형식 또는 충전 금액이 잘못된 경우)",
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
                description = "유저를 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "User not found"
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
    fun chargeBalance(
        @Parameter(
            description = "잔액을 충전할 유저의 ID",
            example = "123",
        )
        @PathVariable userId: Long,
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        )
        @RequestHeader("Queue-Token") token: String,
        @Schema(
            description = "충전할 금액 정보를 포함한 요청 객체",
            example = """
        {
            "amount": 10000
        }
        """,
        )
        @RequestBody request: ChargeBalanceRequest,
    ): ResponseEntity<ChargeBalanceResponse>

    @Operation(summary = "잔액 조회", description = "유저의 잔액을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "유저의 잔액 조회가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChargeBalanceResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "balance": 50000
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
                description = "유저를 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "User not found"
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
    fun getBalance(
        @Parameter(
            description = "잔액을 조회할 유저의 ID",
            example = "123",
        )
        @PathVariable userId: Long,
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        )
        @RequestHeader("Queue-Token") token: String,
    ): ResponseEntity<BalanceResponse>
}
