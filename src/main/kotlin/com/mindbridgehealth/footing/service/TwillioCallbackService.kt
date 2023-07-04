package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwillioDataRepository
import com.mindbridgehealth.footing.data.repository.TwillioStatusRepository
import com.mindbridgehealth.footing.service.entity.TwillioData
import com.mindbridgehealth.footing.service.entity.TwillioStatus
import net.minidev.json.JSONObject
import org.springframework.stereotype.Service

@Service
class TwillioCallbackService(private val twillioStatusRepository: TwillioStatusRepository, private val twillioDataRepository: TwillioDataRepository) {

    fun handleCallback(interviewId: String?,
                       apiVersion: String?,
                       accountSid: String?,
                       callSid: String?,
                       callStatus: String?,
                       recordingSid: String?,
                       recordingUrl: String?,
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
        val twillioStatus = TwillioStatus().apply {
            this.callSid = callSid
            this.callStatus = callStatus
            this.recordingSid = recordingSid
            this.recordingStatus = recordingStatus
            this.transcriptionSid = transcriptionSid
            this.transcriptionStatus = transcriptionStatus
        }

        val twillioJson = JSONObject()
        twillioJson["apiVersion"] = apiVersion
        twillioJson["accountSid"] = accountSid
        twillioJson["callSid"] = callSid
        twillioJson["callStatus"] = callStatus
        twillioJson["recordingSid"] = recordingSid
        twillioJson["recordingUrl"] = recordingUrl
        twillioJson["transcriptionType"] = transcriptionType
        twillioJson["transcriptionUrl"] = transcriptionUrl
        twillioJson["transcriptionSid"] = transcriptionSid
        twillioJson["transcriptionStatus"] = transcriptionStatus
        twillioJson["transcriptionText"] = transcriptionText
        twillioJson["caller"] = caller
        twillioJson["called"] = called
        twillioJson["from"] = from
        twillioJson["to"] = to
        twillioJson["direction"] = direction
        twillioJson["url"] = url

        val twillioData = TwillioData().apply {
            this.rawJson = twillioJson.toJSONString()
        }

        val savedStatus = twillioStatusRepository.save(twillioStatus)
        twillioData.statusId = savedStatus.id
        twillioDataRepository.save(twillioData)

    }
}