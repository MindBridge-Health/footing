package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.TwillioCallbackService
import com.twilio.security.RequestValidator;
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stories")
class StoryController(applicationProperties: ApplicationProperties, val twillioCallbackService: TwillioCallbackService) {

    private val validator: RequestValidator = RequestValidator(applicationProperties.twillioKey)

    //Example Sig: f+WfgQFfSfuN8PszKeX8zBOaroI=
    @PostMapping("/interviews/{id}", consumes = ["application/x-www-form-urlencoded"])
    fun post(
        @PathVariable("id") id: String,
        @RequestParam apiVersion: String,
        @RequestParam transcriptionType: String,
        @RequestParam transcriptionUrl: String,
        @RequestParam transcriptionSid: String,
        @RequestParam called: String,
        @RequestParam recordingSid: String,
        @RequestParam callStatus: String,
        @RequestParam recordingUrl: String,
        @RequestParam from: String,
        @RequestParam direction: String,
        @RequestParam url: String,
        @RequestParam accountSid: String,
        @RequestParam transcriptionText: String,
        @RequestParam caller: String,
        @RequestParam transcriptionStatus: String,
        @RequestParam callSid: String,
        @RequestParam to: String
    ) {
        twillioCallbackService.handleCallback(id,
        apiVersion,
        accountSid,
        callSid,
        callStatus,
        recordingSid,
        recordingUrl,
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
        url)
    }
}