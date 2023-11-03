package com.mindbridgehealth.footing.api.controller

import com.amazonaws.HttpMethod
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mindbridgehealth.footing.api.dto.addpipe.AddPipeEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoConvertedEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoCopiedPipeS3Event
import com.mindbridgehealth.footing.api.dto.addpipe.VideoRecordedEvent
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.MediaService
import com.mindbridgehealth.footing.service.S3UploadService
import com.mindbridgehealth.footing.service.StoryService
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.PermissionValidator
import com.mindbridgehealth.footing.service.util.SignatureGenerator
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.jvm.optionals.getOrElse
import java.time.Duration


@RestController
@RequestMapping("/api/v1/media")
class MediaController(val mediaService: MediaService, val applicationProperties: ApplicationProperties, private val storyService: StoryService, private val s3UploadService: S3UploadService) {

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
        val mediaByStorytellerId = mediaService.findMediaByStorytellerId(Base36Encoder.decodeAltId(id))
        mediaByStorytellerId.map {
            mapMediaLocation(it)
        }
        return mediaByStorytellerId
    }

    @GetMapping("/storytellers/")
    fun getAllMediaForSelf(@AuthenticationPrincipal principal: Jwt): Collection<Media> {
        val mediaByStorytellerId = mediaService.findMediaByStorytellerId(principal.subject)
        mediaByStorytellerId.map {
            mapMediaLocation(it)
        }
        return mediaByStorytellerId
    }

    @GetMapping("/stories/{id}")
    fun getAllMediaForStoryForSelf(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String): Collection<Media> {
        val story = storyService.findStoryByAltId(Base36Encoder.decodeAltId(id)).getOrElse { throw Exception("Story not found") }
        if(Base36Encoder.decodeAltId(story.storyteller.id!!) == principal.subject) {
            val media = mediaService.findMediaByStory(Base36Encoder.decodeAltId(story.id!!))
            media.map {
                mapMediaLocation(it)
            }
            return media
        } else {
            throw Exception("Story not found!") //ToDo: Remove '!'
        }
    }

    @GetMapping("/storytellers/names/{name}/events")
    fun getNewMediaForSelf(@AuthenticationPrincipal principal: Jwt, @PathVariable name: String): Media? {

        var checks = 0
        while(checks < 5) {
            val mediaByStorytellerId = mediaService.findMediaByStorytellerIdAndName(principal.subject, name.replace(".heic", ".jpg").replace(".HEIC", ".jpg"))
            if (mediaByStorytellerId.isPresent) {
                val media = mediaByStorytellerId.get()
                mapMediaLocation(media)
                return media
            }
            checks++
            Thread.sleep(5000) // Sleep for 5 seconds before checking again
        }

        return null
    }

    private fun mapMediaLocation(media: Media) {
        if (media.location.toString().startsWith("s3")) {
            media.location = URI.create(s3UploadService.generatePresignedUrlForObject(media.name, Duration.ofHours(1), HttpMethod.GET))
        }
    }

    @PostMapping("/storytellers/{id}")
    fun postToStoryteller(@RequestBody media: Media, @PathVariable id: String): String {
        return mediaService.associateMediaWithStoryteller(media, id, null)
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
        var validationUrl = getServiceUrl() + request.requestURI

        val signature = SignatureGenerator.generateSignature(applicationProperties.addPipeKey, validationUrl, jsonString)
        var verified: Boolean? = true
        if (xPipeSignature.isNullOrEmpty() || !xPipeSignature.equals(signature)) {
            logger.error("Sig Mismatch xPipe: $xPipeSignature, gen: $signature, url: $validationUrl, json: $jsonString")
            verified = null
        }

        return when (event) {
            is VideoRecordedEvent -> handleVideoRecordedEvent(event, xPipeSignature, verified)
            is VideoConvertedEvent -> handleVideoConvertedEvent(event, xPipeSignature, verified)
            is VideoCopiedPipeS3Event -> handleVideoCopiedPipeS3Event(event, xPipeSignature, verified)
            else -> handleUnknownEvent(event)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String) {
        val media = mediaService.findMediaById(Base36Encoder.decodeAltId(id)).getOrElse { throw Exception("Did not find media") }
        PermissionValidator.assertValidPermissions(media, principal)
        mediaService.deleteMedia(Base36Encoder.decodeAltId(id))
    }

    private fun handleVideoRecordedEvent(event: VideoRecordedEvent, signature: String, verified: Boolean?): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, null, signature, null, verified)
        return "videoRecorded"
    }

    private fun handleVideoConvertedEvent(event: VideoConvertedEvent, signature: String, verified: Boolean?): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, null, signature, null, verified)
        return "videoConverted"
    }

    private fun handleVideoCopiedPipeS3Event(event: VideoCopiedPipeS3Event, signature: String, verified: Boolean?): String {
        handleEventCommon(event, event.data.id, event.data.payload, event.data.videoName, event.data.url, signature, event.data.snapshotUrl, verified)
        return "videoCopied"
    }

    private fun handleEventCommon(event: AddPipeEvent, id: String, payload: String, videoName: String, location: URL?, signature: String, thumbnail: URL?, verified: Boolean?) {
        val payloadMap: Map<String, String> = deserializeKeyValuePairs(payload.substring(1, payload.length - 1))
        val interviewQuestionId = Base36Encoder.decodeAltId(payloadMap["interview_question_id"] ?: throw Exception("Missing interview_question_id from payload"))
        val media = Media(Base36Encoder.encodeAltId("AddPipe|$signature"), videoName, emptyList(), location?.toURI(), "mp4", null, null, thumbnail?.toURI(), verified)

        mediaService.updateMediaStatus(media, interviewQuestionId, event)
    }

    private fun handleUnknownEvent(event: AddPipeEvent): String {
        println("Got unknown event type from AddPipe: " + event.event)
        return "unknown"
    }

    private fun deserializeKeyValuePairs(json: String): Map<String, String> {
        return ObjectMapper().readValue(json, HashMap<String, String>().javaClass);
    }

    private fun getServiceUrl(): String {
        val request: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        var proxyUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()

        if ("prod" == activeProfile) //App Runner is actually running on http with an https LB in front
        {
            proxyUrl = proxyUrl.replace("http", "https")
        }

        return proxyUrl
    }

}