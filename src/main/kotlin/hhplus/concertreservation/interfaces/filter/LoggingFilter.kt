package hhplus.concertreservation.interfaces.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class LoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(wrappedRequest, wrappedResponse)

        logRequest(wrappedRequest)
        logResponse(wrappedResponse)

        wrappedResponse.copyBodyToResponse()
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val requestBody = request.contentAsByteArray
        val body =
            if (requestBody.isNotEmpty()) {
                String(requestBody, StandardCharsets.UTF_8)
            } else {
                "empty"
            }

        log.info("Request URL: ${request.requestURI}")
        log.info("Request Method: ${request.method}")
        log.info("Request Headers: ${request.headerNames.toList().joinToString { "$it: ${request.getHeader(it)}" }}")
        log.info("Request Body: $body")
    }

    private fun logResponse(response: ContentCachingResponseWrapper) {
        val responseBody = response.contentAsByteArray
        val body =
            if (responseBody.isNotEmpty()) {
                String(responseBody, StandardCharsets.UTF_8)
            } else {
                "empty"
            }

        log.info("Response Status: ${response.status}")
        log.info("Response Headers: ${response.headerNames.joinToString { "$it: ${response.getHeader(it)}" }}")
        log.info("Response Body: $body")
    }
}
