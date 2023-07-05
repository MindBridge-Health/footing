package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.TwilioDataRepository
import com.mindbridgehealth.footing.data.repository.TwilioStatusRepository
import com.mindbridgehealth.footing.service.entity.TwilioData
import com.mindbridgehealth.footing.service.entity.TwilioStatus
import io.mockk.every
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.verify

class TwilioCallbackServiceTests {

    @Test
    fun handleCallback_sparseData_sparseJson() {

        val mockDataRepository = mockk<TwilioDataRepository>()
        val mockStatusRepository = mockk<TwilioStatusRepository>()

        val expectedData = TwilioData().apply {
            this.statusId = 1
            this.rawJson = "{\"caller\":\"1231231234\",\"callSid\":\"callsid\",\"called\":\"2342342345\",\"callStatus\":\"completed\",\"accountSid\":\"account1\"}"
        }

        every { mockDataRepository.save(any()) } returnsArgument(0)
        every { mockStatusRepository.save(any())} returns TwilioStatus().apply { id = 1 }

        val twilioCallbackService = TwilioCallbackService(mockStatusRepository, mockDataRepository)
        twilioCallbackService.handleCallback(
            "abc123",
            null,
            "account1",
            "callsid",
            "completed",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "1231231234",
            "2342342345",
            null,
            null,
            null,
            null
        )

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
    }

    @Test
    fun handleCallback_fullData_fullJson() {

        val mockDataRepository = mockk<TwilioDataRepository>()
        val mockStatusRepository = mockk<TwilioStatusRepository>()

        val expectedData = TwilioData().apply {
            this.statusId = 1
            this.rawJson = "{\"callSid\":\"callSid\",\"called\":\"2342342345\",\"transcriptionType\":\"transcriptionType\",\"transcriptionUrl\":\"transcriptionUrl\",\"transcriptionSid\":\"transcriptionSid\",\"transcriptionStatus\":\"transcriptionStatus\",\"url\":\"http:\\/\\/localhost\",\"caller\":\"1231231234\",\"apiVersion\":\"apiVersion\",\"recordingSid\":\"recordingSid\",\"transcriptionText\":\"transcription text\",\"callStatus\":\"completed\",\"recordingUrl\":\"recordingUrl\",\"from\":\"from\",\"to\":\"to\",\"accountSid\":\"account1\",\"direction\":\"direction\"}"
        }

        every { mockDataRepository.save(any()) } returnsArgument(0)
        every { mockStatusRepository.save(any())} returns TwilioStatus().apply { id = 1 }

        val twilioCallbackService = TwilioCallbackService(mockStatusRepository, mockDataRepository)
        twilioCallbackService.handleCallback(
            "abc123",
            "apiVersion",
            "account1",
            "callSid",
            "completed",
            "recordingSid",
            "recordingUrl",
            "recordingStatus",
            "transcriptionType",
            "transcriptionUrl",
            "transcriptionSid",
            "transcriptionStatus",
            "transcription text",
            "1231231234",
            "2342342345",
            "from",
            "to",
            "direction",
            "http://localhost"
        )

        verify { mockDataRepository.save(eq(expectedData)) }
        verify { mockStatusRepository.save(any()) }
    }
}