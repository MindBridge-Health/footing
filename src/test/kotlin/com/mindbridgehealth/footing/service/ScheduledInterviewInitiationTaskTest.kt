package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.model.Question
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ScheduledInterviewInitiationTaskTest {

    private var scheduledInterviewRepository = mockk<ScheduledInterviewRepository>()

    private var interviewService = mockk<InterviewService>()

    private var interviewQuestionService = mockk<InterviewQuestionService>()

    private var smsNotificationService = mockk<SmsNotificationService>()

    private lateinit var scheduledInterviewInitiationTask: ScheduledInterviewInitiationTask

    @BeforeEach
    fun setup() {
        scheduledInterviewInitiationTask = ScheduledInterviewInitiationTask(
            scheduledInterviewRepository,
            interviewService,
            interviewQuestionService,
            smsNotificationService,
            ApplicationProperties("", "", "", "", "", "")
        )
    }

    @AfterEach
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `test initial interview sent`() {
        // Arrange
        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = false, completed = false)
        val interview = createInterviewEntity()
        val interviewQuestion = createInterviewQuestionEntity()
        val interviewQuestionText = "What is your favorite color?"

        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns listOf(scheduledInterviewEntity)
        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns emptyList()
        every { interviewService.findInterviewEntityById(any()) } returns interview
        every { interviewQuestionService.findEntitiesByInterviewId(any()) } returns listOf(interviewQuestion)
        every { smsNotificationService.sendInterviewLink(any(), any(), any(), any(), any(), any()) } just runs
        every { scheduledInterviewRepository.save(any()) } returnsArgument 0

        // Act
        scheduledInterviewInitiationTask.performTask()

        // Assert
        verify(exactly = 1) { smsNotificationService.sendInterviewLink(any(), any(), any(), any(), any(), any()) }
        verify(exactly = 1) { scheduledInterviewRepository.save(any()) }
        verify(exactly = 0) { smsNotificationService.sendMessage(any(), any())}
    }

    @Test
    fun `after 3pm, more than 6 hours after scheduled time, reminder sent`() {
        // Arrange
        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = true, completed = false)
        val interview = createInterviewEntity()
        val name = "John Doe"

        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns listOf(scheduledInterviewEntity)
        every { interviewService.findInterviewEntityById(any()) } returns interview
        every { smsNotificationService.sendMessage(any(), any()) } just runs
        every { scheduledInterviewRepository.delete(any()) } just runs
        every { interviewQuestionService.findEntitiesByInterviewId(any())} returns listOf(createInterviewQuestionEntity())

        // Act
        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
        scheduledInterviewInitiationTask.performTask()

        // Assert
        verify(exactly = 0) { smsNotificationService.sendInterviewLink(any(), any(), any(), any(), any(), any()) }
        verify(exactly = 1) { smsNotificationService.sendMessage(any(), any()) }
        verify(exactly = 1) { scheduledInterviewRepository.delete(any()) }
    }

    @Test
    fun `test reminder not sent if initial interview not sent`() {
        // Arrange
        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = false, completed = false)

        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns emptyList()

        // Act
        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
        scheduledInterviewInitiationTask.performTask()

        // Assert
        verify(exactly = 0) { smsNotificationService.sendMessage(any(), any()) }
        verify(exactly = 0) { scheduledInterviewRepository.delete(any()) }
    }

    @Test
    fun `test reminder not sent if interview completed`() {
        // Arrange
        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = true, completed = true)
        val interview = createInterviewEntity()

        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns listOf(scheduledInterviewEntity)
        every { scheduledInterviewRepository.delete(any()) } just runs
        every { interviewService.findInterviewEntityById(any()) } returns interview
        every { interviewQuestionService.findEntitiesByInterviewId(any())} returns listOf(createInterviewQuestionEntity())

        // Act
        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
        scheduledInterviewInitiationTask.performTask()

        // Assert
        verify(exactly = 0) { smsNotificationService.sendMessage(any(), any()) }
        verify(exactly = 1) { scheduledInterviewRepository.delete(any()) }
    }

    // Helper methods to create entities
    private fun createScheduledInterviewEntity(linkSent: Boolean, completed: Boolean): ScheduledInterviewEntity {
        return ScheduledInterviewEntity().apply {
            this.linkSent = linkSent
            this.interview = createInterviewEntity().apply { this.completed = completed }
            this.scheduledTime = Timestamp.valueOf("2023-07-15 08:00:00")
        }
    }

    private fun createInterviewEntity(): InterviewEntity {
        return InterviewEntity().apply {
            this.id = 1
            this.storyteller = createStorytellerEntity()
        }
    }

    private fun createInterviewQuestionEntity(): InterviewQuestionEntity {
        return InterviewQuestionEntity().apply {
            this.altId = "iq1"
            this.question = QuestionEntity().apply {
                this.altId = "Q1"
                this.text = "What is your favorite color?"
            }
        }
    }

    private fun createStorytellerEntity(): StorytellerEntity {
        return StorytellerEntity().apply {
            this.id = 1
            this.firstname = "John"
            this.mobile = "1234567890"
        }
    }
}
