package hhplus.concertreservation.config

import hhplus.concertreservation.interfaces.interceptor.WaitingQueueTokenInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val waitingQueueTokenInterceptor: WaitingQueueTokenInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(waitingQueueTokenInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/api/waiting-queues", "/actuator", "/actuator/**", "/metrics")
    }
}
