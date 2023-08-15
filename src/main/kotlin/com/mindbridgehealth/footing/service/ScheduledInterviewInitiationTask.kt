package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.ScheduledInterviewEntity
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.twilio.Twilio
import io.awspring.cloud.sqs.operations.SendResult
import io.awspring.cloud.sqs.operations.SqsTemplate
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.*

@Component
class ScheduledInterviewInitiationTask(
    private val scheduledInterviewRepository: ScheduledInterviewRepository,
    private val interviewService: InterviewService,
    private val interviewQuestionService: InterviewQuestionService,
    private val smsNotificationService: SmsNotificationService,
    applicationProperties: ApplicationProperties,
    private val sqsTemplate: SqsTemplate
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    @Value("\${application.sqsUrl}")
    private lateinit var sqsQueueUrl: String
    //Public var to enable testing
    var clock: Clock = Clock.systemDefaultZone()

    init {
        Twilio.init(applicationProperties.twilioSid, applicationProperties.twilioKey)
    }

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
        val result: SendResult<String> = sqsTemplate.send {
            it.queue(sqsQueueUrl)
                .payload("{\"id\": \"${scheduledInterviewEntity.id}\"}")
                .messageGroupId("interview-tasks")
        }
        logger.info("Sent SQS: ${result.messageId}, ${result.additionalInformation} ")
    }

    //Todo: This will have to take user timezone into account at the very least probably reminder preferences too
    //Todo: Quiet hours? e.g. don't send texts between 9pm and 9am local time
    private fun isTimeToSendReminder(scheduledInterviewEntity: ScheduledInterviewEntity): Boolean {
        val currentTime = ZonedDateTime.now(clock)
        val targetTime = scheduledInterviewEntity.scheduledTime?.toInstant()?.plusSeconds(6 * 60 * 60) // 6 hours after scheduled time

        return currentTime.isAfter(targetTime?.atZone(ZoneId.of("America/New_York"))) && currentTime.toLocalTime().isAfter(LocalTime.of(15, 0)) // Check if it's after 3 PM
    }

}