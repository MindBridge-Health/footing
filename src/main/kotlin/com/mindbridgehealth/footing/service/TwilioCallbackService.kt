package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.entity.TwilioData
import com.mindbridgehealth.footing.service.entity.TwilioStatus
import net.minidev.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class TwilioCallbackService(
    private val twilioStatusRepository: TwilioStatusRepository,
    private val twilioDataRepository: TwilioDataRepository,
    private val interviewQuestionService: InterviewQuestionService,
    private val storyService: StoryService
) {

    private val logger = LoggerFactory.getLogger(TwilioCallbackService::class.java)
    fun handleCallback(interviewQuestionId: String, callbackData: Map<String, String>) {
        val twilioStatus = TwilioStatus().apply {
            this.callSid = callbackData["CallSid"]
            this.callStatus = callbackData["CallStatus"]
            this.recordingSid = callbackData["RecordingSid"]
            this.recordingStatus = callbackData["RecordingStatus"]
            this.recordingUrl = callbackData["RecordingUrl"]
            this.transcriptionSid = callbackData["TranscriptionSid"]
            this.transcriptionStatus = callbackData["TranscriptionStatus"]
        }

        val savedStatus = twilioStatusRepository.save(twilioStatus)
        val twilioJson = JSONObject()
        callbackData.entries.forEach { e -> twilioJson[e.key] = e.value }

        val twilioData = TwilioData().apply {
            this.name = "Twilio_${callbackData["x-twilio-signature"]}"
            this.altId = "Twilio|${callbackData["x-twilio-signature"]}"
            this.status = savedStatus
            this.rawJson = twilioJson.toJSONString()
        }

        callbackData["TranscriptionText"]?.let {
            val interviewQuestion = interviewQuestionService.findEntityByAltId(interviewQuestionId).get()
            val storyteller = interviewQuestion.interview?.storyteller ?: throw IllegalStateException("Unable to find storyteller for interview question $interviewQuestionId")

            val storyEntity = StoryEntity().apply {
                this.name = "Twilio_InterviewQuestion_$interviewQuestionId"
                this.text = it
                this.storyteller = storyteller
            }
            val savedStoryEntity = storyService.saveEntity(storyEntity)

            twilioData.apply {
                this.story = savedStoryEntity
                this.interviewQuestion = interviewQuestion
            }
            logger.debug("Saved Story for Twilio Callback")
        }

        twilioDataRepository.save(twilioData)
    }
}