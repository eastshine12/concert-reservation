package hhplus.concertreservation.domain.common.error

import hhplus.concertreservation.domain.common.error.ErrorCode.*
import hhplus.concertreservation.domain.common.error.LogLevel.*

enum class ErrorType(val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    // common
    SYSTEM_FAILURE(SYSTEM_ERROR, "System encountered an unexpected error.", ERROR),
    TOKEN_EXPIRED(AUTHORIZATION_ERROR, "The token has expired.", WARN),

    // WaitingQueue
    INVALID_TOKEN(VALIDATION_ERROR, "Invalid or missing token.", WARN),
    QUEUE_ALREADY_EXISTS(QUEUE_ERROR, "A queue already exists for this token.", WARN),
    NO_QUEUE_FOUND(QUEUE_ERROR, "No queue found for the given token.", WARN),

    // Concert
    INVALID_RESERVATION_STATUS(VALIDATION_ERROR, "Reservation status is invalid.", WARN),
    CONCERT_SCHEDULE_SOLD_OUT(BUSINESS_ERROR, "The concert schedule is sold out.", INFO),
    SEAT_UNAVAILABLE(BUSINESS_ERROR, "The seat is not available for reservation.", WARN),
    NO_CONCERT_FOUND(BUSINESS_ERROR, "No concert found for the given ID.", WARN),
    NO_CONCERT_SCHEDULE_FOUND(BUSINESS_ERROR, "No concert schedule found for the given ID.", WARN),
    NO_RESERVATION_FOUND(BUSINESS_ERROR, "No reservation found for the given ID.", WARN),
    NO_SEAT_FOUND(BUSINESS_ERROR, "No seat found for the given ID.", WARN),

    // User
    INVALID_BALANCE_AMOUNT(VALIDATION_ERROR, "Invalid balance amount provided.", WARN),
    USER_NOT_FOUND(BUSINESS_ERROR, "User not found.", WARN),
    INSUFFICIENT_BALANCE(BUSINESS_ERROR, "User has insufficient balance.", WARN),

    // Payment
    PAYMENT_ALREADY_PROCESSED(BUSINESS_ERROR, "Payment has already been processed.", INFO),
    PAYMENT_FAILED(BUSINESS_ERROR, "Payment failed.", WARN),
}
