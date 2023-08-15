package com.mindbridgehealth.footing.service

import com.amazonaws.services.sqs.model.Message
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.*
import io.awspring.cloud.sqs.operations.SendResult
import io.awspring.cloud.sqs.operations.SqsSendOptions
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals

class ScheduledInterviewInitiationTaskTest {

//    private var scheduledInterviewRepository = mockk<ScheduledInterviewRepository>()
//
//    private lateinit var scheduledInterviewInitiationTask: ScheduledInterviewInitiationTask
//
//    private var sqsTemplate = mockk<SqsTemplate>(relaxed = true)
//
//
//    @BeforeEach
//    fun setup() {
//        scheduledInterviewInitiationTask = ScheduledInterviewInitiationTask(
//            scheduledInterviewRepository,
//            sqsTemplate
//        )
//    }
//
//    @AfterEach
//    fun cleanup() {
//        unmockkAll()
//    }
//
//    @Test
//    fun `test initial interview sent`() {
//        // Arrange
//        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = false, completed = false)
//
//        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns listOf(scheduledInterviewEntity)
//        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns emptyList()
//        every { scheduledInterviewRepository.save(any()) } returnsArgument 0
//        val stringSendResult = mockk<SendResult<String>>()
//        every { stringSendResult.messageId } returns UUID.randomUUID()
//        every { stringSendResult.additionalInformation } returns mockk()
//
//        val expectedPayload = "{\"id\": \"${scheduledInterviewEntity.id}\", \"tags\": [{\"text\": \"initial\"}]}"
//        //every { sqsTemplate.send<String>(any()) } returns stringSendResult
//        every { sqsTemplate.hint(String::class).send<String>(captureLambda()) } answers {
//            val capturedLambda = firstArg<(SqsTemplate) -> Unit>()
//            val mockBuilder = mockk<SqsTemplate>()
//            capturedLambda(mockBuilder) // Call the captured lambda with the mock builder
//            // Now you can access and perform assertions on the mockBuilder's methods
//            verify { mockBuilder.payload(ofType(String::class)) } // For example, verify payload method
//            SendResult(UUID.randomUUID(), "", class MyMessage : Mesage, "")
//        }
//
//        // Act
//        scheduledInterviewInitiationTask.performTask()
//
//        // Assert
//        verify(exactly = 1) { scheduledInterviewRepository.findAllByScheduledTime(any()) }
//        verify(exactly = 1) { scheduledInterviewRepository.findAllByLinkSent(any()) }
//        verify(exactly = 1) { sqsTemplate.send<String>(any()) }
//        verify { sqsTemplate.send<String>(match { it.invoke() == expectedPayload }) }
//    }
//
//    @Test
//    fun `after 3pm, more than 6 hours after scheduled time, reminder sent`() {
//        // Arrange
//        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = true, completed = false)
//        val interview = createInterviewEntity()
//        val name = "John Doe"
//
//        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
//        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns listOf(scheduledInterviewEntity)
//        every { scheduledInterviewRepository.delete(any()) } just runs
//
//        // Act
//        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
//        scheduledInterviewInitiationTask.performTask()
//
//        // Assert
//        verify(exactly = 1) { scheduledInterviewRepository.findAllByScheduledTime(any()) }
//        verify(exactly = 1) { scheduledInterviewRepository.findAllByLinkSent(any()) }
//        verify (exactly = 1) { sqsTemplate.send<String>(any()) }
//    }

//    @Test
//    fun `test reminder not sent if initial interview not sent`() {
//        // Arrange
//        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = false, completed = false)
//
//        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
//        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns emptyList()
//
//        // Act
//        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
//        scheduledInterviewInitiationTask.performTask()
//
//        // Assert
//        verify(exactly = 0) { smsNotificationService.sendMessage(any(), any()) }
//        verify(exactly = 0) { scheduledInterviewRepository.delete(any()) }
//    }
//
//    @Test
//    fun `test reminder not sent if interview completed`() {
//        // Arrange
//        val scheduledInterviewEntity = createScheduledInterviewEntity(linkSent = true, completed = true)
//        val interview = createInterviewEntity()
//
//        every { scheduledInterviewRepository.findAllByScheduledTime(any()) } returns emptyList()
//        every { scheduledInterviewRepository.findAllByLinkSent(true) } returns listOf(scheduledInterviewEntity)
//        every { scheduledInterviewRepository.delete(any()) } just runs
//        every { interviewService.findInterviewEntityById(any()) } returns interview
//        every { interviewQuestionService.findEntitiesByInterviewId(any())} returns listOf(createInterviewQuestionEntity())
//
//        // Act
//        scheduledInterviewInitiationTask.clock = Clock.fixed(ZonedDateTime.parse("2023-07-15T15:30-04:00[America/New_York]").toInstant(), ZoneId.of("America/New_York"))
//        scheduledInterviewInitiationTask.performTask()
//
//        // Assert
//        verify(exactly = 0) { smsNotificationService.sendMessage(any(), any()) }
//        verify(exactly = 1) { scheduledInterviewRepository.delete(any()) }
//    }

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
