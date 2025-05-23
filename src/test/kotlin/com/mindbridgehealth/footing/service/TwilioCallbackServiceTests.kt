package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.*
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.assertEquals

class TwilioCallbackServiceTests {

    private val mockDataRepository = mockk<TwilioDataRepository>()
    private val mockStatusRepository = mockk<TwilioStatusRepository>()
    private val mockInterviewQuestionService = mockk<InterviewQuestionService>()
    private val mockStoryService = mockk<StoryService>()
    private val mockSmsNotificationService = mockk<SmsNotificationService>()
    private val mockInterviewService = mockk<InterviewService>()


    @Test
    fun handleCallback_callComplete_callJson_noNotify() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)


        every { mockDataRepository.save(any()) } returnsArgument (0)
        val statusEntityCaptureSlot = CapturingSlot<TwilioStatus>()
        every { mockStatusRepository.save(capture(statusEntityCaptureSlot)) } answers  { statusEntityCaptureSlot.captured.apply { this.id = 1 }}
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "st1"
        }
        val interviewQuestionEntity = InterviewQuestionEntity().apply {
            this.interview = InterviewEntity(
                "i1",
                "Interview 1",
                null,
                false,
                null,
                storytellerEntity,
            )
        }
        every { mockInterviewQuestionService.findEntityByAltId(any())} returns Optional.of(interviewQuestionEntity)
        every { mockSmsNotificationService.sendMessage(any(), any()) }

        val paramMap = HashMap<String, String>()
        paramMap["AccountSid"] = "account1"
        paramMap["CallSid"] = "callSid"
        paramMap["CallStatus"] = "completed"
        paramMap["Caller"] = "1231231234"
        paramMap["Called"] = "2342342345"
        paramMap["x-twilio-signature"] = "xSig"

        val twilioCallbackService =
            TwilioCallbackService(
                mockStatusRepository,
                mockDataRepository,
                mockInterviewQuestionService,
                mockStoryService,
                mockSmsNotificationService,
                mockInterviewService
            )
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )

        verify { mockDataRepository.save(any())}
        verify { mockStatusRepository.save(any()) }
        verify { mockSmsNotificationService wasNot called }
    }

    @Test
    fun handleCallback_callRecorded_recordedJson_notify() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)


        val dataCapturingSlot = CapturingSlot<TwilioData>()
        every { mockDataRepository.save(capture(dataCapturingSlot)) } answers {dataCapturingSlot.captured.apply { this.id = 2 }}
        val statusEntityCaptureSlot = CapturingSlot<TwilioStatus>()
        every { mockStatusRepository.save(capture(statusEntityCaptureSlot)) } answers  { statusEntityCaptureSlot.captured.apply { this.id = 1 }}
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "st1"
            this.firstname = "Joe"
        }
        val interviewQuestionEntity = InterviewQuestionEntity().apply {
            this.altId = "55da68f2"
            this.interview = InterviewEntity(
                null,
                "null",
                null,
                false,
                null,
                storytellerEntity
            )
        }
        every { mockInterviewQuestionService.findEntityByAltId(any()) } returns Optional.of(interviewQuestionEntity)
        every { mockSmsNotificationService.sendMessage(any(), any()) } returns Unit

        val rawJson = "{\"ApiVersion\":\"apiVersion\",\"Called\":\"2342342345\",\"CallStatus\":\"callStatus\",\"RecordingSid\":\"recordingSid\",\"RecordingStatus\":\"complete\",\"RecordingUrl\":\"recordingUrl\",\"From\":\"from\",\"x-twilio-signature\":\"xSig\",\"Direction\":\"direction\",\"AccountSid\":\"account1\",\"Url\":\"http:\\/\\/localhost\",\"Caller\":\"1231231234\",\"CallSid\":\"callSid\",\"To\":\"to\"}"
        val expectedData = TwilioData().apply {
            this.id = 2
            this.status = TwilioStatus().apply {
                this.id = 1
                this.callSid = "callSid"
                this.callStatus = "callStatus"
                this.recordingSid = "recordingSid"
                this.recordingStatus = "complete"
                this.recordingUrl = "recordingUrl"
            }
            this.name = "Twilio_xSig"
            this.altId = "Twilio|xSig"
            this.rawJson = rawJson
        }

        //Weird shortcut to get a Map from the JSON
        class B: TypeReference<HashMap<String, String>>()
        val typeRef = B()
        val paramMap: HashMap<String, String> = ObjectMapper().readValue(rawJson, typeRef)

        val twilioCallbackService =
            TwilioCallbackService(
                mockStatusRepository,
                mockDataRepository,
                mockInterviewQuestionService,
                mockStoryService,
                mockSmsNotificationService,
                mockInterviewService
            )
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )
        //Useful for debugging
//        println(ObjectMapper().writeValueAsString(expectedData))
//        println(ObjectMapper().writeValueAsString(dataCapturingSlot.captured))
        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
        verify { mockSmsNotificationService.sendMessage(any(), any()) }
    }

    @Test
    fun handleCallbackFindInterview_transcriptionComplete_saveStory_markInterviewComplete_noNotify() {
        mockDataRepository.clear(MockkClear.BEFORE)
        mockStatusRepository.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)
        mockInterviewService.clear(MockkClear.BEFORE)


        val dataCapturingSlot = CapturingSlot<TwilioData>()
        every { mockDataRepository.save(capture(dataCapturingSlot)) } answers {dataCapturingSlot.captured.apply { this.id = 2 }}
        val statusEntityCaptureSlot = CapturingSlot<TwilioStatus>()
        every { mockStatusRepository.save(capture(statusEntityCaptureSlot)) } answers  { statusEntityCaptureSlot.captured.apply { this.id = 1 }}
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "st1"
        }
        val interviewEntity = InterviewEntity(
            "i1",
            "Interview 1",
            null,
            false,
            null,
            storytellerEntity,
        ).apply { this.id = 5 }
        val interviewQuestionEntity = InterviewQuestionEntity().apply {
            this.interview = interviewEntity
        }
        every { mockInterviewQuestionService.findEntityByAltId(any())} returns Optional.of(interviewQuestionEntity)
        every { mockInterviewQuestionService.save(any<InterviewQuestionEntity>())} returns interviewQuestionEntity
        val storyEntityCaptureSlot = CapturingSlot<StoryEntity>()
        every { mockStoryService.saveEntity(capture(storyEntityCaptureSlot)) } answers {storyEntityCaptureSlot.captured.apply { this.id = 1 }}
        every { mockSmsNotificationService.sendInterviewLink(any(), any(), any(), any(), any(), any()) } just runs
        every { mockInterviewService.markInterviewComplete(any()) } just runs

        //Weird shortcut to get a Map from the JSON
        val rawJson = "{\"ApiVersion\":\"apiVersion\",\"TranscriptionType\":\"transcriptionType\",\"TranscriptionUrl\":\"transcriptionUrl\",\"TranscriptionSid\":\"transcriptionSid\",\"Called\":\"2342342345\",\"CallStatus\":\"callStatus\",\"RecordingSid\":\"recordingSid\",\"RecordingStatus\":\"complete\",\"RecordingUrl\":\"recordingUrl\",\"From\":\"from\",\"x-twilio-signature\":\"xSig\",\"Direction\":\"direction\",\"AccountSid\":\"account1\",\"Url\":\"http:\\/\\/localhost\",\"TranscriptionText\":\"transcriptionText\",\"Caller\":\"1231231234\",\"TranscriptionStatus\":\"transcriptionStatus\",\"CallSid\":\"callSid\",\"To\":\"to\"}"
        class B: TypeReference<HashMap<String, String>>()
        val typeRef = B()
        val paramMap: HashMap<String, String> = ObjectMapper().readValue(rawJson, typeRef)

        val twilioCallbackService =
            TwilioCallbackService(
                mockStatusRepository,
                mockDataRepository,
                mockInterviewQuestionService,
                mockStoryService,
                mockSmsNotificationService,
                mockInterviewService
            )
        twilioCallbackService.handleCallback(
            "abc123",
            paramMap
        )

        val expectedData = TwilioData().apply {
            this.id = 2
            this.status = TwilioStatus().apply {
                this.id = 1
                this.callSid = "callSid"
                this.callStatus = "callStatus"
                this.recordingSid = "recordingSid"
                this.recordingStatus = "complete"
                this.recordingUrl = "recordingUrl"
                this.transcriptionSid = "transcriptionSid"
                this.transcriptionStatus = "transcriptionStatus"
            }
            this.name = "Twilio_xSig"
            this.altId = "Twilio|xSig"
            this.rawJson = rawJson
            this.story = storyEntityCaptureSlot.captured
            this.interviewQuestion = interviewQuestionEntity
        }

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
        verify { mockSmsNotificationService wasNot called }
        verify { mockInterviewService.markInterviewComplete(eq(interviewEntity.id!!)) }

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