package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
class ScheduledInterviewInitiationTask(private val scheduledInterviewRepository: ScheduledInterviewRepository) {

    private val logger = LoggerFactory.getLogger(ScheduledInterviewInitiationTask::class.java)
    @Scheduled(cron = "0 * * * * *")
    fun performTask() {
        val scheduledInterviews = scheduledInterviewRepository.findAllByScheduledTime(Timestamp.from(Instant.now()))
        logger.debug("Looking for scheduled interviews")
        scheduledInterviews.forEach { scheduledInterviewEntity ->
            logger.debug("Would execute ${scheduledInterviewEntity.name}")
            scheduledInterviewRepository.delete(scheduledInterviewEntity)
        }

    }
}