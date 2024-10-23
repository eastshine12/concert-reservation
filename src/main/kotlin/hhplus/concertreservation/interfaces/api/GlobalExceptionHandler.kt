package hhplus.concertreservation.interfaces.api

import hhplus.concertreservation.domain.common.error.ErrorCode
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.error.LogLevel.*
import hhplus.concertreservation.domain.common.exception.CoreException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(
        ex: CoreException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorType: ErrorType = ex.errorType
        val detailsMessage = ex.details?.toString() ?: "No details available"

        when (errorType.logLevel) {
            INFO -> log.info("CoreException: [${errorType.code}] ${ex.message}. Details: $detailsMessage")
            WARN -> log.warn("CoreException: [${errorType.code}] ${ex.message}. Details: $detailsMessage")
            ERROR -> log.error("CoreException: [${errorType.code}] ${ex.message}. Details: $detailsMessage", ex)
        }

        val errorResponse =
            ErrorResponse(
                status = getHttpStatusByErrorCode(errorType.code).value(),
                errorCode = errorType.code.name,
                message = ex.message.toString(),
                path = request.servletPath,
                details = ex.details,
            )

        return ResponseEntity.status(getHttpStatusByErrorCode(errorType.code)).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val status =
            when (ex) {
                is IllegalStateException -> HttpStatus.CONFLICT
                is AccessDeniedException -> HttpStatus.FORBIDDEN
                is IllegalArgumentException -> HttpStatus.BAD_REQUEST
                is NoSuchElementException -> HttpStatus.NOT_FOUND
                is UnsupportedOperationException -> HttpStatus.NOT_IMPLEMENTED
                is NullPointerException -> HttpStatus.INTERNAL_SERVER_ERROR
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            }

        val errorMessage =
            when (ex) {
                is IllegalStateException -> "Invalid state detected."
                is AccessDeniedException -> "Access is denied."
                is IllegalArgumentException -> "Invalid argument provided."
                is NoSuchElementException -> "Requested element not found."
                is UnsupportedOperationException -> "Operation not supported."
                is NullPointerException -> "A null pointer exception occurred."
                else -> "An unexpected error occurred."
            }

        log.error("Exception: [${status.name}] ${ex::class.simpleName} - $errorMessage.", ex)

        val errorResponse =
            ErrorResponse(
                status = status.value(),
                errorCode = "SYSTEM_ERROR",
                message = errorMessage,
                path = request.servletPath,
            )

        return ResponseEntity.status(status).body(errorResponse)
    }

    private fun getHttpStatusByErrorCode(errorCode: ErrorCode): HttpStatus {
        return when (errorCode) {
            ErrorCode.VALIDATION_ERROR -> HttpStatus.BAD_REQUEST
            ErrorCode.SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            ErrorCode.AUTHENTICATION_ERROR -> HttpStatus.UNAUTHORIZED
            ErrorCode.AUTHORIZATION_ERROR -> HttpStatus.FORBIDDEN
            ErrorCode.DATABASE_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            ErrorCode.BUSINESS_ERROR -> HttpStatus.CONFLICT
            ErrorCode.QUEUE_ERROR -> HttpStatus.CONFLICT
        }
    }
}

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val status: Int,
    val errorCode: String,
    val message: String,
    val path: String? = null,
    val details: Any? = null,
)
