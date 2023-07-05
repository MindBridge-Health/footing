package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.TwilioCallbackService
import com.twilio.security.RequestValidator;
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stories")
class StoryController(applicationProperties: ApplicationProperties, val twilioCallbackService: TwilioCallbackService) {

    private val validator: RequestValidator = RequestValidator(applicationProperties.twillioKey)

    //Example Sig: f+WfgQFfSfuN8PszKeX8zBOaroI=
    @PostMapping("/interviews/{id}", consumes = ["application/x-www-form-urlencoded"])
    fun post(
        @PathVariable("id") id: String,
        @RequestParam("ApiVersion") apiVersion: String?,
        @RequestParam("TranscriptionType") transcriptionType: String?,
        @RequestParam("TranscriptionUrl") transcriptionUrl: String?,
        @RequestParam("TranscriptionSid") transcriptionSid: String?,
        @RequestParam("Called") called: String?,
        @RequestParam("RecordingSid") recordingSid: String?,
        @RequestParam("CallStatus") callStatus: String?,
        @RequestParam("RecordingUrl") recordingUrl: String?,
        @RequestParam("RecordingStatus") recordingStatus: String?,
        @RequestParam("From") from: String?,
        @RequestParam("Direction") direction: String?,
        @RequestParam("url") url: String?,
        @RequestParam("AccountSid") accountSid: String?,
        @RequestParam("TranscriptionText") transcriptionText: String?,
        @RequestParam("Caller") caller: String?,
        @RequestParam("TranscriptionStatus") transcriptionStatus: String?,
        @RequestParam("CallSid") callSid: String?,
        @RequestParam("To") to: String?
    ) {
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
}