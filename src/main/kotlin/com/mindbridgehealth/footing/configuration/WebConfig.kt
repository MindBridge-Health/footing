package com.mindbridgehealth.footing.configuration

import net.minidev.json.writer.BeansMapper.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequestLoggingInterceptor())
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/fwd").setViewName("forward:/index.html")
    }
}