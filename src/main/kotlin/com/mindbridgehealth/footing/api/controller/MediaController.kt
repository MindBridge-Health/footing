package com.mindbridgehealth.footing.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.api.dto.addpipe.AddPipeEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoConvertedEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoCopiedPipeS3Event
import com.mindbridgehealth.footing.api.dto.addpipe.VideoRecordedEvent
import com.mindbridgehealth.footing.service.MediaService
import com.mindbridgehealth.footing.service.model.Media
import org.springframework.web.bind.annotation.*
import java.net.URL


@RestController
@RequestMapping("/media")
class MediaController(val mediaService: MediaService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String) = mediaService.findMediaById(id)
    @PostMapping("/storytellers/{id}")
    fun postToStoryteller(@RequestBody media: Media, @PathVariable id: String): String = mediaService.associateMediaWithStoryteller(media, id)

    @PostMapping("/")
    fun addPipeCallback(@RequestBody event: AddPipeEvent): String {
        return when (event) {
            is VideoRecordedEvent -> handleVideoRecordedEvent(event)
            is VideoConvertedEvent -> handleVideoConvertedEvent(event)
            is VideoCopiedPipeS3Event -> handleVideoCopiedPipeS3Event(event)
            else -> handleUnknownEvent(event)
        }
    }

    private fun handleVideoRecordedEvent(event: VideoRecordedEvent): String {
        handleEventCommon(event, event.data.payload, event.data.videoName, null)
        return "videoRecorded"
    }

    private fun handleVideoConvertedEvent(event: VideoConvertedEvent): String {
        handleEventCommon(event, event.data.payload, event.data.videoName, null)
        return "videoConverted"
    }

    private fun handleVideoCopiedPipeS3Event(event: VideoCopiedPipeS3Event): String {
        handleEventCommon(event, event.data.payload, event.data.videoName, event.data.rawRecordingUrl)
        return "videoCopied"
    }

    private fun handleEventCommon(event: AddPipeEvent, payload: String, videoName: String, location: URL?) {
        val payloadMap: Map<String, String> = deserializeKeyValuePairs(payload)
        val interviewQuestionId = payloadMap["interview_question_id"] ?: throw Exception("Missing interview_question_id from payload")
        val media = Media(null, videoName, emptyList(), location?.toURI(), "type", null, null, event.event)
        mediaService.associateMediaWithStorytellerFromInterviewQuestion(media, interviewQuestionId)
    }

    private fun handleUnknownEvent(event: AddPipeEvent): String {
        println("Got unknown event type from AddPipe: " + event.event)
        return "unknown"
    }

    private fun deserializeKeyValuePairs(json: String): Map<String, String> {
        return ObjectMapper().readValue(json, HashMap<String, String>().javaClass);
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: String) {
        mediaService.deleteMedia(id)    }

//    @PostMapping("/story/{id}")
//    fun postToStory(media: Media, @PathVariable id: UUID) = mediaService.catalogMedia(media, id)
}