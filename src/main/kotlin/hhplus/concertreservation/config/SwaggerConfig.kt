package hhplus.concertreservation.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("콘서트 예약 시스템 API 문서")
                    .description("이 문서는 콘서트 예약 시스템의 API 엔드포인트에 대한 설명을 제공합니다.")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("김동현")
                            .email("eastshine_@naver.com")
                            .url("https://github.com/eastshine12"),
                    ),
            )
    }
}
