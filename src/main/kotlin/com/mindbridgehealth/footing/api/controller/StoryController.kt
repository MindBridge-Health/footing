package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.TwilioCallbackService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.twilio.security.RequestValidator
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLDecoder

@RestController
@RequestMapping("/api/v1/stories")
class StoryController(applicationProperties: ApplicationProperties, val twilioCallbackService: TwilioCallbackService) {

    private val validator: RequestValidator = RequestValidator(applicationProperties.twilioKey)
    private val logger = LoggerFactory.getLogger(StoryController::class.java)

    @Value("\${spring.profiles.active}")
    private val activeProfile: String? = null
    @PostMapping("/interviews/{id}", consumes = ["application/x-www-form-urlencoded"])
    fun post(
        @PathVariable("id") id: String,
        @RequestBody body: String,
    ): ResponseEntity<Unit> {

        val request: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val xTwilioSignature = request.getHeader("x-twilio-signature")

        val parameters = UriComponentsBuilder.fromUriString("?$body").build().queryParams
        val paramMap = decodeParameters(parameters, "UTF-8")

        var validationUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + request.requestURI
        if("prod" == activeProfile) //App Runner is actually running on http with an https LB in front
        {
            validationUrl = validationUrl.replace("http", "https")
        }
        logger.info("url: $validationUrl")
        if(!validator.validate(validationUrl, paramMap, xTwilioSignature)) {
            logger.error("Invalid Signature: $xTwilioSignature\"")
            return ResponseEntity.badRequest().build()
        }
        
        twilioCallbackService.handleCallback( Base36Encoder.decodeAltId(id), paramMap)
        return ResponseEntity.ok().build()
    }

    private fun decodeParameters(parameters: MultiValueMap<String, String>, encoding: String): Map<String, String> {
        val paramMap = HashMap<String, String>()
        for (entry in parameters.entries) {
            val key = entry.key
            val value = entry.value.firstOrNull()?.let { URLDecoder.decode(it, encoding) }
            if (value != null) {
                paramMap[key] = value
            }
        }
        return paramMap
    }
}