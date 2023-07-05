package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.TwilioData
import com.mindbridgehealth.footing.service.entity.TwilioStatus
import net.minidev.json.JSONObject
import org.springframework.stereotype.Service

@Service
class TwilioCallbackService(private val twilioStatusRepository: TwilioStatusRepository, private val twilioDataRepository: TwilioDataRepository) {

    fun handleCallback(
        interviewId: String?,
        apiVersion: String?,
        accountSid: String?,
        callSid: String?,
        callStatus: String?,
        recordingSid: String?,
        recordingUrl: String?,
        recordingStatus: String?,
        transcriptionType: String?,
        transcriptionUrl: String?,
        transcriptionSid: String?,
        transcriptionStatus: String?,
        transcriptionText: String?,
        caller: String?,
        called: String?,
        from: String?,
        to: String?,
        direction: String?,
        url: String?,
                       ) {
        val twilioStatus = TwilioStatus().apply {
            this.callSid = callSid
            this.callStatus = callStatus
            this.recordingSid = recordingSid
            this.recordingStatus = recordingStatus
            this.transcriptionSid = transcriptionSid
            this.transcriptionStatus = transcriptionStatus
        }

        val twilioJson = JSONObject()
        if(apiVersion != null) twilioJson["apiVersion"] = apiVersion
        if(accountSid != null) twilioJson["accountSid"] = accountSid
        if(callSid != null) twilioJson["callSid"] = callSid
        if(callStatus != null) twilioJson["callStatus"] = callStatus
        if(recordingSid != null) twilioJson["recordingSid"] = recordingSid
        if(recordingUrl != null) twilioJson["recordingUrl"] = recordingUrl
        if(transcriptionType != null) twilioJson["transcriptionType"] = transcriptionType
        if(transcriptionUrl != null) twilioJson["transcriptionUrl"] = transcriptionUrl
        if(transcriptionSid != null) twilioJson["transcriptionSid"] = transcriptionSid
        if(transcriptionStatus != null) twilioJson["transcriptionStatus"] = transcriptionStatus
        if(transcriptionText != null) twilioJson["transcriptionText"] = transcriptionText
        if(caller != null) twilioJson["caller"] = caller
        if(called != null) twilioJson["called"] = called
        if(from != null) twilioJson["from"] = from
        if(to != null) twilioJson["to"] = to
        if(direction != null) twilioJson["direction"] = direction
        if(url != null) twilioJson["url"] = url

        val savedStatus = twilioStatusRepository.save(twilioStatus)
        val twilioData = TwilioData().apply {
            this.statusId = savedStatus.id
            this.rawJson = twilioJson.toJSONString()
        }
        twilioDataRepository.save(twilioData)

    }
}