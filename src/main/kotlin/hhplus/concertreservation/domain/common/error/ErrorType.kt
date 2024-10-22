package hhplus.concertreservation.domain.common.error

import hhplus.concertreservation.domain.common.error.ErrorCode.*
import hhplus.concertreservation.domain.common.error.LogLevel.*

enum class ErrorType(val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    // common
    SYSTEM_FAILURE(SYSTEM_ERROR, "System encountered an unexpected error.", ERROR),
    UNAUTHORIZED_ACCESS(AUTHORIZATION_ERROR, "Unauthorized access to the queue.", WARN),
    TOKEN_EXPIRED(AUTHORIZATION_ERROR, "The token has expired.", WARN),

    // WaitingQueue
    INVALID_TOKEN(VALIDATION_ERROR, "Invalid or missing token.", WARN),
    INVALID_QUEUE_POSITION(VALIDATION_ERROR, "Invalid queue position.", WARN),
    QUEUE_ALREADY_EXISTS(QUEUE_ERROR, "A queue already exists for this token.", WARN),
    NO_QUEUE_FOUND(QUEUE_ERROR, "No queue found for the given token.", WARN),

    // Concert
    INVALID_CONCERT_ID(VALIDATION_ERROR, "Invalid concert ID provided.", WARN),
    INVALID_SEAT_NUMBER(VALIDATION_ERROR, "Invalid seat number.", WARN),
    INVALID_RESERVATION_STATUS(VALIDATION_ERROR, "Reservation status is invalid.", WARN),
    CONCERT_SOLD_OUT(BUSINESS_ERROR, "The concert is sold out.", INFO),
    SEAT_UNAVAILABLE(BUSINESS_ERROR, "The seat is not available for reservation.", WARN),
    NO_CONCERT_FOUND(BUSINESS_ERROR, "No concert found for the given ID.", WARN),
    NO_CONCERT_SCHEDULE_FOUND(BUSINESS_ERROR, "No concert schedule found for the given ID.", WARN),
    NO_RESERVATION_FOUND(BUSINESS_ERROR, "No reservation found for the given ID.", WARN),
    NO_SEAT_FOUND(BUSINESS_ERROR, "No seat found for the given ID.", WARN),
    NO_SEATS_FOUND(BUSINESS_ERROR, "No seats found for the given schedule.", WARN),

    // User
    INVALID_USER_ID(VALIDATION_ERROR, "Invalid user ID provided.", WARN),
    INVALID_BALANCE_AMOUNT(VALIDATION_ERROR, "Invalid balance amount provided.", WARN),
    USER_NOT_FOUND(BUSINESS_ERROR, "User not found.", WARN),
    INSUFFICIENT_BALANCE(BUSINESS_ERROR, "User has insufficient balance.", WARN),

    // Payment
    INVALID_PAYMENT_AMOUNT(VALIDATION_ERROR, "Invalid payment amount provided.", WARN),
    PAYMENT_ALREADY_PROCESSED(BUSINESS_ERROR, "Payment has already been processed.", INFO),
}
