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
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalField

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
        scheduledInterviews
            .filter { scheduledInterviewEntity -> !scheduledInterviewEntity.linkSent!! }
            .forEach { scheduledInterviewEntity -> processScheduledInterview(scheduledInterviewEntity) }

        logger.debug("Looking for overdue scheduled interviews")
        val potentialOverdueInterviews = scheduledInterviewRepository.findAllByLinkSent(true)
        potentialOverdueInterviews
            .filter { scheduledInterviewEntity -> isTimeToSendReminder(scheduledInterviewEntity) }
            .forEach { scheduledInterviewEntity -> processScheduledInterview(scheduledInterviewEntity) }
    }

    private fun processScheduledInterview(scheduledInterviewEntity: ScheduledInterviewEntity) {
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

        val encodedQuestion =
            URLEncoder.encode(interviewQuestionText, StandardCharsets.UTF_8.toString())
        val url = "http://app.mindbridgehealth.com/interview.html?cid=6w7x8y9z&mid=1a2b3c4b&rid=12345&rtel=$number&question=$encodedQuestion&interview_id=$interviewAltId&interview_question_id=$interviewQuestionId"

        if(!scheduledInterviewEntity.linkSent!!) {
            val questionString =
                "Here is your question for your MindBridge Health interview: $interviewQuestionText"
            logger.debug("Sending initial interview: ${scheduledInterviewEntity.name}")
            smsNotificationService.sendInterviewLink(number!!, name, questionString, url, interviewAltId, interviewQuestionId)

            scheduledInterviewEntity.linkSent = true
            scheduledInterviewRepository.save(scheduledInterviewEntity)
        } else {
            if(scheduledInterviewEntity.interview!!.completed == false) {
                logger.debug("Reminding interview: ${scheduledInterviewEntity.name}")
                val message = "Hello $name! \uD83C\uDF1F Your unique memories are like stars waiting to shine. \uD83C\uDF20 We'd love to hear your response to this week's question! Your story is invaluable. Take a moment to share your wisdom. \uD83D\uDCDD\uD83E\uDDE1 http://app.mindbridgehealth.com/interview.html?cid=6w7x8y9z&mid=1a2b3c4b&rid=12345&rtel=$number&question=$encodedQuestion&interview_id=$interviewAltId&interview_question_id=$interviewQuestionId"
                smsNotificationService.sendMessage(number!!, message)
            }
            scheduledInterviewRepository.delete(scheduledInterviewEntity)
        }

    }

    //Todo: This will have to take user timezone into account at the very least probably reminder preferences too
    fun isTimeToSendReminder(scheduledInterviewEntity: ScheduledInterviewEntity): Boolean {
        val currentTime = ZonedDateTime.now(ZoneId.of("UTC"))
        val targetTime = ZonedDateTime.of(currentTime.toLocalDate(), LocalTime.of(15, 0), ZoneId.of("America/New_York"))

        return scheduledInterviewEntity.linkSent!! && currentTime.isAfter(targetTime)
    }

    private fun logAndThrow(errorMsg: String) {
        logger.error(errorMsg)
        throw IllegalStateException(errorMsg)
    }
}