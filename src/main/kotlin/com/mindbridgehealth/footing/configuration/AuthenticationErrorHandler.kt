package com.mindbridgehealth.footing.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.api.dto.ErrorMessage
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class AuthenticationErrorHandler(val mapper: ObjectMapper) : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        val errorMessage = ErrorMessage("Requires authentication")
        val json = mapper!!.writeValueAsString(errorMessage)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(json)
        response.flushBuffer()
    }
}