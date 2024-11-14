package hhplus.concertreservation.infrastructure.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DataPlatformApiClient {
    private val log = LoggerFactory.getLogger(this::class.java)
    fun sendReservation() {
        log.info("외부 API 호출: 예약 전송 이벤트 처리 완료")
    }
}
