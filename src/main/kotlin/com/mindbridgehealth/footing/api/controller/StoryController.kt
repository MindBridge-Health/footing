package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.TwilioCallbackService
import com.twilio.security.RequestValidator
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
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
    ) {

        logger.info("id: $id")
        logger.info("body: $body")

        val request: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val xTwilioSignature = request.getHeader("x-twilio-signature")

        logger.info("sig: $xTwilioSignature")
        val parameters = UriComponentsBuilder.fromUriString("?$body").build().queryParams
        val paramMap = decodeParameters(parameters, "UTF-8")

        val apiVersion = paramMap["ApiVersion"]
        val transcriptionType = paramMap["TranscriptionType"]
        val transcriptionUrl = paramMap["TranscriptionUrl"]
        val transcriptionSid = paramMap["TranscriptionSid"]
        val called = paramMap["Called"]
        val recordingSid = paramMap["RecordingSid"]
        val callStatus = paramMap["CallStatus"]
        val recordingUrl = paramMap["RecordingUrl"]
        val recordingStatus = paramMap["RecordingStatus"]
        val from = paramMap["From"]
        val direction = paramMap["Direction"]
        val url = paramMap["url"]
        val accountSid = paramMap["AccountSid"]
        val transcriptionText = paramMap["TranscriptionText"]
        val caller =  paramMap["Caller"]
        val transcriptionStatus = paramMap["TranscriptionStatus"]
        val callSid = paramMap["CallSid"]
        val to = paramMap["To"]

        var validationUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + request.requestURI
        if("prod" == activeProfile) //App Runner is actually running on http with an https LB in front
        {
            validationUrl = validationUrl.replace("http", "https")
        }
        logger.info("url: $validationUrl")
        if(!validator.validate(validationUrl, paramMap, xTwilioSignature)) throw Exception("Invalid Signature")
        
        twilioCallbackService.handleCallback(
            id,
            apiVersion,
            accountSid,
            callSid,
            callStatus,
            recordingSid,
            recordingUrl,
            recordingStatus,
            transcriptionType,
            transcriptionUrl,
            transcriptionSid,
            transcriptionStatus,
            transcriptionText,
            caller,
            called,
            from,
            to,
            direction,
            url
        )
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