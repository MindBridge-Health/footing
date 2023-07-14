package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.ScheduledInterviewEntity
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.twilio.Twilio
import com.twilio.rest.studio.v2.flow.ExecutionCreator
import com.twilio.type.PhoneNumber
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.Instant

@Component
class ScheduledInterviewInitiationTask(
    private val scheduledInterviewRepository: ScheduledInterviewRepository,
    private val interviewService: InterviewService,
    private val interviewQuestionService: InterviewQuestionService,
    applicationProperties: ApplicationProperties
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        Twilio.init(applicationProperties.twilioSid, applicationProperties.twilioKey)
    }

    @Scheduled(cron = "0 * * * * *")
    fun performTask() {
        val scheduledInterviews = scheduledInterviewRepository.findAllByScheduledTime(Timestamp.from(Instant.now()))
        logger.debug("Looking for scheduled interviews")
        scheduledInterviews.forEach { scheduledInterviewEntity ->
            processScheduledInterview(scheduledInterviewEntity)
        }
    }

    private fun processScheduledInterview(scheduledInterviewEntity: ScheduledInterviewEntity) {
        logger.debug("Reminding Interview: ${scheduledInterviewEntity.name}")
        val interview = scheduledInterviewEntity.interview?.altId?.let { interviewService.findInterviewEntityByAltId(it) } //ToDo Consider lookup by id (indexed) rather than altId (not indexed)
        val interviewQuestions = interview?.id?.let { interviewQuestionService.findEntitiesByInterviewId(it) }
        if(interviewQuestions.isNullOrEmpty()) {
            logAndThrow("Interview had no questions associated with it ${scheduledInterviewEntity.interview?.id}")
        }
        val interviewAltId = Base36Encoder.encodeAltId(scheduledInterviewEntity.interview?.altId!!)

        val interviewQuestionText = interview?.id?.let { interviewQuestionService.findEntitiesByInterviewId(it) }?.first()?.question?.text
        if (interviewQuestionText == null) {
            logAndThrow("Unable to find question for scheduled interview: ${scheduledInterviewEntity.name} Interview Id: ${scheduledInterviewEntity.interview?.id}")
        }

        val number = interview?.storyteller?.mobile
        val name = interview?.storyteller?.firstname ?: ""
        if (number == null) {
            logAndThrow("Unable to find mobile number for Storyteller ${interview?.storyteller?.id} scheduled interview: ${scheduledInterviewEntity.name} Interview Id: ${scheduledInterviewEntity.interview?.id}")
        }
        val questionString =
            "Here is your question for your upcoming MindBridge Health interview: $interviewQuestionText"
        val encodedQuestion =
            URLEncoder.encode(interviewQuestionText, StandardCharsets.UTF_8.toString())
        val url =
            "http://54.196.211.99/e-c/message.html?cid=6w7x8y9z&mid=1a2b3c4b&rid=12345&rtel=$number&question=$encodedQuestion&interview_id=$interviewAltId"

        val parameters = HashMap<String, Any>()
        parameters["firstname"] = name
        parameters["question"] = questionString
        parameters["interviewurl"] = url
        parameters["interview_id"] = interviewAltId

        sendTwilioReminder(number!!, parameters, scheduledInterviewEntity)

        scheduledInterviewRepository.delete(scheduledInterviewEntity)
    }

    private fun logAndThrow(errorMsg: String) {
        logger.error(errorMsg)
        throw IllegalStateException(errorMsg)
    }

    private fun sendTwilioReminder(
        number: String,
        parameters: HashMap<String, Any>,
        scheduledInterviewEntity: ScheduledInterviewEntity
    ) {
        val client =
            ExecutionCreator("FW11eb3d59f134dc3ef56211df72adc21d", PhoneNumber(number), PhoneNumber("+18443555050"))
        client.setParameters(parameters)
        val execution = client.create()
        logger.info("Kicked off Twilio Reminder, SID: ${execution.sid}, Interview: ${scheduledInterviewEntity.name}")
    }
}