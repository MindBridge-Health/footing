package com.mindbridgehealth.footing.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mindbridgehealth.footing.api.dto.addpipe.AddPipeEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoConvertedEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoCopiedPipeS3Event
import com.mindbridgehealth.footing.api.dto.addpipe.VideoRecordedEvent
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.MediaService
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.PermissionValidator
import com.mindbridgehealth.footing.service.util.SignatureGenerator
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URL
import java.util.*
import kotlin.jvm.optionals.getOrElse


@RestController
@RequestMapping("/api/v1/media")
class MediaController(val mediaService: MediaService, val applicationProperties: ApplicationProperties) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    @Value("\${spring.profiles.active}")
    private val activeProfile: String? = null
    private val objectMapper = jacksonObjectMapper()
    @GetMapping("/{id}")
    fun get(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String): Media {
        val media = mediaService.findMediaById(Base36Encoder.decodeAltId(id)).getOrElse { throw Exception("Did not find media") }
        PermissionValidator.assertValidPermissions(media, principal)
        return media
    }

    @GetMapping("/storytellers/{id}")
    fun getAllMediaForStoryteller(@PathVariable id: String): Collection<Media> {
        return mediaService.findMediaByStorytellerId(Base36Encoder.decodeAltId(id))
    }
    @PostMapping("/storytellers/{id}")
    fun postToStoryteller(@RequestBody media: Media, @PathVariable id: String): String {
        return mediaService.associateMediaWithStoryteller(media, id)
    }

    @PostMapping("/")
    fun addPipeCallback(@RequestBody jsonString: String): String {
        val request: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val xPipeSignature = request.getHeader("X-Pipe-Signature")
        val event: AddPipeEvent = try {objectMapper.readValue(jsonString, AddPipeEvent::class.java)}
        catch (e: Exception) {
            logger.error("Error handling Media", e)
            throw HttpClientErrorException(HttpStatusCode.valueOf(400))}
        var validationUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + request.requestURI

        if("prod" == activeProfile) //App Runner is actually running on http with an https LB in front
        {
            validationUrl = validationUrl.replace("http", "https")
        }
        val signature = SignatureGenerator.generateSignature(applicationProperties.addPipeKey, validationUrl, jsonString)
        if (xPipeSignature.isNullOrEmpty() || !xPipeSignature.equals(signature)) {
            throw Exception("Signature did not match!")
        }

        return when (event) {
            is VideoRecordedEvent -> handleVideoRecordedEvent(event, xPipeSignature)
            is VideoConvertedEvent -> handleVideoConvertedEvent(event, xPipeSignature)
            is VideoCopiedPipeS3Event -> handleVideoCopiedPipeS3Event(event, xPipeSignature)
            else -> handleUnknownEvent(event)
        }
    }

    private fun handleVideoRecordedEvent(event: VideoRecordedEvent, signature: String): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, null, signature)
        return "videoRecorded"
    }

    private fun handleVideoConvertedEvent(event: VideoConvertedEvent, signature: String): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, null, signature)
        return "videoConverted"
    }

    private fun handleVideoCopiedPipeS3Event(event: VideoCopiedPipeS3Event, signature: String): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, event.data.url, signature)
        return "videoCopied"
    }

    private fun handleEventCommon(event: AddPipeEvent, id: String, payload: String, videoName: String, location: URL?, signature: String) {
        val payloadMap: Map<String, String> = deserializeKeyValuePairs(payload.substring(1, payload.length - 1))
        val interviewQuestionId = Base36Encoder.decodeAltId(payloadMap["interview_question_id"] ?: throw Exception("Missing interview_question_id from payload"))
        val media = Media(Base36Encoder.encodeAltId("AddPipe|$signature"), videoName, emptyList(), location?.toURI(), "type", null, null)
        mediaService.updateMediaStatus(media, interviewQuestionId, event)
    }

    private fun handleUnknownEvent(event: AddPipeEvent): String {
        println("Got unknown event type from AddPipe: " + event.event)
        return "unknown"
    }

    private fun deserializeKeyValuePairs(json: String): Map<String, String> {
        return ObjectMapper().readValue(json, HashMap<String, String>().javaClass);
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String) {
        val media = mediaService.findMediaById(Base36Encoder.decodeAltId(id)).getOrElse { throw Exception("Did not find media") }
        PermissionValidator.assertValidPermissions(media, principal)
        mediaService.deleteMedia(Base36Encoder.decodeAltId(id))
    }
}