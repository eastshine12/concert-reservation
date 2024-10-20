package hhplus.concertreservation.interfaces.api.payment

import hhplus.concertreservation.interfaces.api.payment.dto.req.PaymentRequest
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentHistoryResponse
import hhplus.concertreservation.interfaces.api.payment.dto.res.PaymentResponse
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "결제 API", description = "결제 처리 및 결제 내역 조회 API")
interface IPaymentController {
    @Operation(summary = "결제 처리", description = "예약 정보를 통해 결제를 처리합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "결제 처리가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChargeBalanceResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "paymentId": 1,
                                    "amount": 70000,
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
                description = "유저 또는 예약 정보를 찾을 수 없는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "status": "failed",
                                    "message": "User(Reservation) not found"
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
    fun processPayment(
        @Parameter(
            description = "대기열 토큰. 요청 인증에 사용됩니다.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
        )
        @RequestHeader("Queue-Token") token: String,
        @Schema(
            description = "결제에 필요한 정보를 포함한 요청 객체",
            example = """
        {
            "userId": 123,
            "reservationId": 4
        }
        """,
        )
        @RequestBody request: PaymentRequest,
    ): ResponseEntity<PaymentResponse>

    @Operation(summary = "결제 내역 조회", description = "유저의 결제 내역을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 내역 조회가 성공적으로 처리된 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChargeBalanceResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                    "payments": [
                                        {
                                          "id": 3,
                                          "price": 110000,
                                          "status": "success"
                                        },
                                        {
                                          "id": 4,
                                          "price": 90000,
                                          "status": "success"
                                        }
                                    ]
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
    fun getPaymentHistory(
        @Parameter(
            description = "결제 내역을 조회할 유저의 ID",
            example = "123",
        )
        @RequestParam userId: Long,
    ): ResponseEntity<PaymentHistoryResponse>
}
