package com.mindbridgehealth.footing.configuration

import com.mindbridgehealth.footing.data.repository.SignatureRepository
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.SignatureGenerator
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.time.Instant

@Component
class SignatureValidationFilter(
    private val applicationProperties: ApplicationProperties,
    private val signatureRepository: SignatureRepository
) : OncePerRequestFilter() {

    @Value("\${spring.profiles.active}")
    private val activeProfile: String? = null

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI == "/userhome.html") {
            if (request.queryString != null) {
                val signature = extractSignatureFromRequest(request)
                val updatedQueryString = removeXsigParameterFromQueryString(request.queryString)
                val updatedURI = request.requestURI + updatedQueryString
                var validationUrl =
                    ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + updatedURI
                if ("prod" == activeProfile) //App Runner is actually running on http with an https LB in front
                {
                    validationUrl = validationUrl.replace("http", "https")
                }
                if (SignatureGenerator.validateSignature(applicationProperties.mbhKey, validationUrl, "", signature)) {
                    val optSignatureEntity = signatureRepository.findBySignature(signature)
                    optSignatureEntity.ifPresentOrElse({
                        if (it.url == validationUrl && it.userAltId == extractUserIdFromRequest(request)) {
                            filterChain.doFilter(request, response) // Proceed with serving the static asset
                        }
                    }, { response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature") })
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature")
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature")
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }

    private fun extractUserIdFromRequest(request: HttpServletRequest): String {
        val userId = request.getParameter("userId")
        return if (userId != null) {
            Base36Encoder.decodeAltId(userId)
        } else
            ""
    }

    private fun extractSignatureFromRequest(request: HttpServletRequest): String {
        return request.getParameter("xsig") ?: ""
    }

    private fun removeXsigParameterFromQueryString(queryString: String): String {
        val pattern = "&?xsig=[^&]+".toRegex()
        return queryString.replace(pattern, "")
    }

}
