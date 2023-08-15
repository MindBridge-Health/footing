package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.ScheduledInterviewEntity
import com.twilio.Twilio
import io.awspring.cloud.sqs.operations.SendResult
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.*

@Component
class ScheduledInterviewInitiationTask(
    private val scheduledInterviewRepository: ScheduledInterviewRepository,
    private val sqsTemplate: SqsTemplate
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    @Value("\${application.sqsUrl}")
    private lateinit var sqsQueueUrl: String
    //Public var to enable testing
    var clock: Clock = Clock.systemDefaultZone()

    @Scheduled(cron = "0 * * * * *")
    fun performTask() {
        val scheduledInterviews = scheduledInterviewRepository.findAllByScheduledTime(Timestamp.from(Instant.now().plusSeconds(60)))
        logger.debug("Looking for scheduled interviews")
        scheduledInterviews
            .filter { scheduledInterviewEntity -> !scheduledInterviewEntity.linkSent!! }
            .forEach { scheduledInterviewEntity -> processScheduledInterview(scheduledInterviewEntity, "initial") }

        logger.debug("Looking for overdue scheduled interviews")
        val potentialOverdueInterviews = scheduledInterviewRepository.findAllByLinkSent(true)
        potentialOverdueInterviews
            .filter { scheduledInterviewEntity -> isTimeToSendReminder(scheduledInterviewEntity) }
            .forEach { scheduledInterviewEntity -> processScheduledInterview(scheduledInterviewEntity, "reminder") }
    }

    private fun processScheduledInterview(scheduledInterviewEntity: ScheduledInterviewEntity, tagText: String) {
        val result: SendResult<String> = sqsTemplate.send {
            it.queue(sqsQueueUrl)
                .payload("{\"id\": \"${scheduledInterviewEntity.id}\", \"tags\": [\"text\": \"${tagText}\"]}") //FIFO Q will dedupe based on content, so we need to differentiate between initial and reminder messages
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