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
    private val smsNotificationService: SmsNotificationService,
    applicationProperties: ApplicationProperties
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        Twilio.init(applicationProperties.twilioSid, applicationProperties.twilioKey)
    }

    //ToDo This needs to account for concurrency
    @Scheduled(cron = "0 * * * * *")
    fun performTask() {
        val scheduledInterviews = scheduledInterviewRepository.findAllByScheduledTime(Timestamp.from(Instant.now().plusSeconds(60)))
        logger.debug("Looking for scheduled interviews")
        scheduledInterviews.forEach { scheduledInterviewEntity ->
            processScheduledInterview(scheduledInterviewEntity)
        }
    }

    private fun processScheduledInterview(scheduledInterviewEntity: ScheduledInterviewEntity) {
        logger.debug("Reminding Interview: ${scheduledInterviewEntity.name}")
        val interview = scheduledInterviewEntity.interview?.id?.let { interviewService.findInterviewEntityById(it) }
        val interviewQuestions = interview?.id?.let { interviewQuestionService.findEntitiesByInterviewId(it) }
        if(interviewQuestions.isNullOrEmpty()) {
            logAndThrow("Interview had no questions associated with it ${scheduledInterviewEntity.interview?.id}")
        }
        val interviewAltId = Base36Encoder.encodeAltId(scheduledInterviewEntity.interview?.altId!!)

        val interviewQuestion = interview?.id?.let { interviewQuestionService.findEntitiesByInterviewId(it) }?.first()
        if (interviewQuestion == null) {
            logAndThrow("Unable to find question for scheduled interview: ${scheduledInterviewEntity.name} Interview Id: ${scheduledInterviewEntity.interview?.id}")
        }
        val interviewQuestionId = Base36Encoder.encodeAltId(interviewQuestion?.altId!!)
        val interviewQuestionText = interviewQuestion.question?.text

        val number = interview.storyteller?.mobile
        val name = interview.storyteller?.firstname ?: ""
        if (number == null) {
            logAndThrow("Unable to find mobile number for Storyteller ${interview.storyteller?.id} scheduled interview: ${scheduledInterviewEntity.name} Interview Id: ${scheduledInterviewEntity.interview?.id}")
        }
        val questionString =
            "Here is your question for your MindBridge Health interview: $interviewQuestionText"
        val encodedQuestion =
            URLEncoder.encode(interviewQuestionText, StandardCharsets.UTF_8.toString())
        val url =
            "http://app.mindbridgehealth.com/interview.html?cid=6w7x8y9z&mid=1a2b3c4b&rid=12345&rtel=$number&question=$encodedQuestion&interview_id=$interviewAltId&interview_question_id=$interviewQuestionId"

        smsNotificationService.sendInterviewLink(number!!, name, questionString, url, interviewAltId, interviewQuestionId)

        scheduledInterviewRepository.delete(scheduledInterviewEntity)
    }

    private fun logAndThrow(errorMsg: String) {
        logger.error(errorMsg)
        throw IllegalStateException(errorMsg)
    }
}