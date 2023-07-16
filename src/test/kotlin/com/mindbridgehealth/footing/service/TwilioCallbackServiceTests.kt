package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.CapturingSlot
import io.mockk.every
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.assertEquals

class TwilioCallbackServiceTests {

    private val mockDataRepository = mockk<TwilioDataRepository>()
    private val mockStatusRepository = mockk<TwilioStatusRepository>()
    private val mockInterviewService = mockk<InterviewQuestionService>()
    private val mockStoryService = mockk<StoryService>()


    @Test
    fun handleCallback_sparseData_sparseJson() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)

        val expectedData = TwilioData().apply {
            this.statusId = 1
            this.rawJson =
                "{\"Called\":\"2342342345\",\"Caller\":\"1231231234\",\"CallStatus\":\"completed\",\"CallSid\":\"callsid\",\"AccountSid\":\"account1\"}"
        }

        every { mockDataRepository.save(any()) } returnsArgument (0)
        every { mockStatusRepository.save(any()) } returns TwilioStatus().apply { id = 1 }
        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.of(InterviewQuestionEntity().apply { this.interview = InterviewEntity(
            null,
            null,
            null,
            false,
            null,
            null
        )})

        val paramMap = HashMap<String, String>()
        paramMap["AccountSid"] = "account1"
        paramMap["CallSid"] = "callsid"
        paramMap["CallStatus"] = "completed"
        paramMap["Caller"] = "1231231234"
        paramMap["Called"] = "2342342345"

        val twilioCallbackService =
            TwilioCallbackService(mockStatusRepository, mockDataRepository, mockInterviewService, mockStoryService)
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
    }

    @Test
    fun handleCallback_fullData_fullJson() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)

        val expectedData = TwilioData().apply {
            this.statusId = 1
            this.rawJson =
                "{\"ApiVersion\":\"apiVersion\",\"TranscriptionType\":\"transcriptionType\",\"TranscriptionUrl\":\"transcriptionUrl\",\"TranscriptionSid\":\"transcriptionSid\",\"Called\":\"2342342345\",\"CallStatus\":\"completed\",\"RecordingSid\":\"recordingSid\",\"RecordingUrl\":\"recordingUrl\",\"From\":\"from\",\"Direction\":\"direction\",\"AccountSid\":\"account1\",\"Url\":\"http:\\/\\/localhost\",\"TranscriptionText\":\"transcriptionText\",\"Caller\":\"1231231234\",\"TranscriptionStatus\":\"transcriptionStatus\",\"CallSid\":\"callSid\",\"To\":\"to\"}"
        }

        every { mockDataRepository.save(any()) } returnsArgument (0)
        every { mockStatusRepository.save(any()) } returns TwilioStatus().apply { id = 1 }
        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.of(InterviewQuestionEntity().apply { this.interview = InterviewEntity(
            null,
            null,
            null,
            false,
            null,
            StorytellerEntity().apply { this.id = 1; this.altId = "S1" }
        )})
        every { mockStoryService.saveEntity(any()) } returnsArgument 0

        val paramMap = HashMap<String, String>()
        paramMap["ApiVersion"] = "apiVersion"
        paramMap["AccountSid"] = "account1"
        paramMap["CallSid"] = "callSid"
        paramMap["CallStatus"] = "completed"
        paramMap["RecordingSid"] = "recordingSid"
        paramMap["RecordingUrl"] = "recordingUrl"
        paramMap["TranscriptionType"] = "transcriptionType"
        paramMap["TranscriptionUrl"] = "transcriptionUrl"
        paramMap["TranscriptionSid"] = "transcriptionSid"
        paramMap["TranscriptionStatus"] = "transcriptionStatus"
        paramMap["TranscriptionText"] = "transcriptionText"
        paramMap["Caller"] = "1231231234"
        paramMap["Called"] = "2342342345"
        paramMap["From"] = "from"
        paramMap["To"] = "to"
        paramMap["Direction"] = "direction"
        paramMap["Url"] = "http://localhost"

        val twilioCallbackService =
            TwilioCallbackService(mockStatusRepository, mockDataRepository, mockInterviewService, mockStoryService)
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
    }

    @Test
    fun handleCallbackFindInterview_saveStory_success() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)

        val expectedData = TwilioData().apply {
            this.statusId = 1
            this.rawJson =
                "{\"ApiVersion\":\"apiVersion\",\"TranscriptionType\":\"transcriptionType\",\"TranscriptionUrl\":\"transcriptionUrl\",\"TranscriptionSid\":\"transcriptionSid\",\"Called\":\"2342342345\",\"CallStatus\":\"completed\",\"RecordingSid\":\"recordingSid\",\"RecordingUrl\":\"recordingUrl\",\"From\":\"from\",\"Direction\":\"direction\",\"AccountSid\":\"account1\",\"Url\":\"http:\\/\\/localhost\",\"TranscriptionText\":\"transcriptionText\",\"Caller\":\"1231231234\",\"TranscriptionStatus\":\"transcriptionStatus\",\"CallSid\":\"callSid\",\"To\":\"to\"}"
        }

        every { mockDataRepository.save(any()) } returnsArgument (0)
        every { mockStatusRepository.save(any()) } returns TwilioStatus().apply { id = 1 }
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "st1"
        }
        every { mockInterviewService.findEntityByAltId(any())} returns Optional.of(InterviewQuestionEntity().apply { this.interview = InterviewEntity(
            "i1",
            "Interview 1",
            null,
            false,
            null,
            storytellerEntity,
        )})
        val storyEntityCaptureSlot = CapturingSlot<StoryEntity>()
        every { mockStoryService.saveEntity(capture(storyEntityCaptureSlot)) } returnsArgument 0

        val paramMap = HashMap<String, String>()
        paramMap["ApiVersion"] = "apiVersion"
        paramMap["AccountSid"] = "account1"
        paramMap["CallSid"] = "callSid"
        paramMap["CallStatus"] = "completed"
        paramMap["RecordingSid"] = "recordingSid"
        paramMap["RecordingUrl"] = "recordingUrl"
        paramMap["TranscriptionType"] = "transcriptionType"
        paramMap["TranscriptionUrl"] = "transcriptionUrl"
        paramMap["TranscriptionSid"] = "transcriptionSid"
        paramMap["TranscriptionStatus"] = "transcriptionStatus"
        paramMap["TranscriptionText"] = "transcriptionText"
        paramMap["Caller"] = "1231231234"
        paramMap["Called"] = "2342342345"
        paramMap["From"] = "from"
        paramMap["To"] = "to"
        paramMap["Direction"] = "direction"
        paramMap["Url"] = "http://localhost"

        val twilioCallbackService =
            TwilioCallbackService(mockStatusRepository, mockDataRepository, mockInterviewService, mockStoryService)
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }

        val expectedStoryEntity = StoryEntity().apply {
            this.name = "Twilio_InterviewQuestion_abc123"
            this.text = paramMap["TranscriptionText"]
            this.storyteller = storytellerEntity
        }

        assertEquals(expectedStoryEntity.name, storyEntityCaptureSlot.captured.name)
        assertEquals(expectedStoryEntity.text, storyEntityCaptureSlot.captured.text)
        assertEquals(expectedStoryEntity.storyteller?.altId, storyEntityCaptureSlot.captured.storyteller?.altId)

    }
}