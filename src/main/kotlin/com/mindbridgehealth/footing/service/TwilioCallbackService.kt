package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.entity.TwilioData
import com.mindbridgehealth.footing.service.entity.TwilioStatus
import net.minidev.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TwilioCallbackService(
    private val twilioStatusRepository: TwilioStatusRepository,
    private val twilioDataRepository: TwilioDataRepository,
    private val interviewService: InterviewService,
    private val storyService: StoryService
) {

    private val logger = LoggerFactory.getLogger(TwilioCallbackService::class.java)
    fun handleCallback(interviewId: String, callbackData: Map<String, String>) {
        val twilioStatus = TwilioStatus().apply {
            this.callSid = callbackData["CallSid"]
            this.callStatus = callbackData["CallStatus"]
            this.recordingSid = callbackData["RecordingSid"]
            this.recordingStatus = callbackData["RecordingStatus"]
            this.transcriptionSid = callbackData["TranscriptionSid"]
            this.transcriptionStatus = callbackData["TranscriptionStatus"]
        }

        val twilioJson = JSONObject()
        callbackData.entries.forEach { e -> twilioJson[e.key] = e.value }

        val savedStatus = twilioStatusRepository.save(twilioStatus)
        val twilioData = TwilioData().apply {
            this.statusId = savedStatus.id
            this.rawJson = twilioJson.toJSONString()
        }
        twilioDataRepository.save(twilioData)


        callbackData["TranscriptionText"]?.let {
            val storyteller = interviewService.findInterviewEntityByAltId(interviewId).storyteller
            if (storyteller != null) {
                val storyEntity = StoryEntity().apply {
                    this.name = "Twilio_Interview_$interviewId"
                    this.text = it
                    this.storyteller = storyteller
                }
                storyService.saveEntity(storyEntity)
                logger.debug("Saved Story for Twilio Callback")
            } else {
                logger.error("Unable to find storyteller for interview $interviewId")
            }
            null
        }
    }
}