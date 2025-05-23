package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.TwilioCallbackService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.Mockito.atLeast
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StoryControllerTests {

    @Autowired
    lateinit var storyController: StoryController

    @MockBean
    lateinit var twilioCallbackService: TwilioCallbackService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var applicationProperties: ApplicationProperties

    @Test
    fun callbackDeserialization_transcriptionComplete_Success() {

        val body =
            "ApiVersion=2010-04-01&TranscriptionType=fast&TranscriptionUrl=https%3A%2F%2Fapi.twilio.com%2F2010-04-01%2FAccounts%2FACcc830b11ab88f5dd8ccc1430511c2357%2FRecordings%2FRE64545a8c9fbe9d6d191c2423c2c993ce%2FTranscriptions%2FTR5b7449e016d0716e82afae470221c007&TranscriptionSid=TR5b7449e016d0716e82afae470221c007&Called=%2B19788356697&RecordingSid=RE64545a8c9fbe9d6d191c2423c2c993ce&CallStatus=completed&RecordingUrl=https%3A%2F%2Fapi.twilio.com%2F2010-04-01%2FAccounts%2FACcc830b11ab88f5dd8ccc1430511c2357%2FRecordings%2FRE64545a8c9fbe9d6d191c2423c2c993ce&From=%2B18443555050&Direction=outbound-api&url=https%3A%2F%2Fwebhook.site%2Fac9cab2b-ff6e-4e88-bf12-eb2bf33cb772%2Fl01xt1w8u21s&AccountSid=ACcc830b11ab88f5dd8ccc1430511c2357&TranscriptionText=One%20of%20my%20fondest%20memories%20was%20going%20and%20getting%20ice%20cream%20as%20a%20little%20child%20with%20my%20parents%20and%20sick%20and%20brothers.%20My%20favorite%20ice%20cream%20was%20from%20a%20place%20called%20cabins.%20I%20would%20order%20orange%20pineapple%20ice%20cream%2C%20with%20pineapple%20topping%2C%20with%20cream%2C%20and%20a%20churning.%20It%20was%20really%20enjoyable.&Caller=%2B18443555050&TranscriptionStatus=completed&CallSid=CAb0b939e902e7a396204cd7ba8c85acbb&To=%2B19788356697"

        Mockito.doAnswer {
            assertEquals("336bacde", it.getArgument<String>(0))
            val paramMap = it.getArgument<Map<String, String>>(1)
            assertEquals("2010-04-01", paramMap["ApiVersion"])
            assertEquals("fast", paramMap["TranscriptionType"])
            assertEquals("https://api.twilio.com/2010-04-01/Accounts/ACcc830b11ab88f5dd8ccc1430511c2357/Recordings/RE64545a8c9fbe9d6d191c2423c2c993ce/Transcriptions/TR5b7449e016d0716e82afae470221c007", paramMap["TranscriptionUrl"])
            assertEquals("TR5b7449e016d0716e82afae470221c007", paramMap["TranscriptionSid"])
            assertEquals("+19788356697", paramMap["Called"])
            assertEquals("RE64545a8c9fbe9d6d191c2423c2c993ce", paramMap["RecordingSid"])
            assertEquals("completed", paramMap["CallStatus"])
            assertEquals("https://api.twilio.com/2010-04-01/Accounts/ACcc830b11ab88f5dd8ccc1430511c2357/Recordings/RE64545a8c9fbe9d6d191c2423c2c993ce", paramMap["RecordingUrl"])
            assertEquals("+18443555050", paramMap["From"])
            assertEquals("outbound-api", paramMap["Direction"])
            assertEquals("https://webhook.site/ac9cab2b-ff6e-4e88-bf12-eb2bf33cb772/l01xt1w8u21s", paramMap["url"])
            assertEquals("ACcc830b11ab88f5dd8ccc1430511c2357", paramMap["AccountSid"])
            assertEquals(
                "One of my fondest memories was going and getting ice cream as a little child with my parents and sick and brothers. My favorite ice cream was from a place called cabins. I would order orange pineapple ice cream, with pineapple topping, with cream, and a churning. It was really enjoyable.",
                paramMap["TranscriptionText"]
            )
            assertEquals("+18443555050", paramMap["Caller"])
            assertEquals("completed", paramMap["TranscriptionStatus"])
            assertEquals("CAb0b939e902e7a396204cd7ba8c85acbb", paramMap["CallSid"])
            assertEquals("+19788356697", paramMap["To"])
            null
        }.`when`(
            twilioCallbackService
        ).handleCallback(
            anyString(),
            anyMap()
        )

        val parameters = UriComponentsBuilder.fromUriString(
            URLDecoder.decode("?$body", "UTF-8")).build().queryParams
        mockMvc.perform(
            post("/api/v1/stories/interview_questions/l01xt1w8u21s")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(body)
                .header(
                    "x-twilio-signature",
                    getValidationSignature("http://localhost/api/v1/stories/interview_questions/l01xt1w8u21s", parameters.toSingleValueMap())
                )
        ).andExpect(MockMvcResultMatchers.status().isOk())

    }

    @Test
    fun callbackDeserialization_RecordingComplete_Success() {

        val body =
            "RecordingSource=RecordVerb&RecordingSid=RE64545a8c9fbe9d6d191c2423c2c993ce&RecordingUrl=https%3A%2F%2Fapi.twilio.com%2F2010-04-01%2FAccounts%2FACcc830b11ab88f5dd8ccc1430511c2357%2FRecordings%2FRE64545a8c9fbe9d6d191c2423c2c993ce&RecordingStatus=completed&RecordingChannels=1&ErrorCode=0&CallSid=CAb0b939e902e7a396204cd7ba8c85acbb&RecordingStartTime=Mon%2C%2003%20Jul%202023%2021%3A45%3A28%20%2B0000&AccountSid=ACcc830b11ab88f5dd8ccc1430511c2357&RecordingDuration=21"

        Mockito.doAnswer {
            assertEquals("336bacde", it.getArgument(0))

            val paramMap = it.getArgument<Map<String, String>>(1)
            assertEquals("ACcc830b11ab88f5dd8ccc1430511c2357", paramMap["AccountSid"])
            assertEquals("CAb0b939e902e7a396204cd7ba8c85acbb", paramMap["CallSid"])
            assertEquals("RE64545a8c9fbe9d6d191c2423c2c993ce", paramMap["RecordingSid"])
            assertEquals("https://api.twilio.com/2010-04-01/Accounts/ACcc830b11ab88f5dd8ccc1430511c2357/Recordings/RE64545a8c9fbe9d6d191c2423c2c993ce",
                paramMap["RecordingUrl"]
            )
            assertEquals("completed", paramMap["RecordingStatus"])

            null
        }.`when`(
            twilioCallbackService
        ).handleCallback(
            anyString(),
            anyMap()
        )

        val parameters = UriComponentsBuilder.fromUriString(
            URLDecoder.decode("?$body", "UTF-8")).build().queryParams
        mockMvc.perform(
            post("/api/v1/stories/interview_questions/l01xt1w8u21s")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(body)
                .header(
                    "x-twilio-signature",
                    getValidationSignature("http://localhost/api/v1/stories/interview_questions/l01xt1w8u21s", parameters.toSingleValueMap())
                )
        ).andExpect(MockMvcResultMatchers.status().isOk())

        Mockito.verify(twilioCallbackService, atLeast(1))
            .handleCallback(
                anyString(),
                anyMap()
            )
    }

    @Test
    fun callbackDeserialization_CallComplete_Success() {

        val body = "Name=l01xt1w8u21s"

        val parameters = UriComponentsBuilder.fromUriString(
            URLDecoder.decode("?$body", "UTF-8")).build().queryParams
        mockMvc.perform(
            post("/api/v1/stories/interview_questions/l01xt1w8u21s")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(body)
                .header(
                    "x-twilio-signature",
                    getValidationSignature("http://localhost/api/v1/stories/interview_questions/l01xt1w8u21s", parameters.toSingleValueMap())
                )
        ).andExpect(MockMvcResultMatchers.status().isOk())
    }

    fun getValidationSignature(url: String?, params: Map<String, String?>?): String? {
        return try {
            val builder = StringBuilder(url)
            if (params != null) {
                val sortedKeys: List<String> = ArrayList(params.keys)
                Collections.sort(sortedKeys)
                for (key in sortedKeys) {
                    builder.append(key)
                    val value = params[key]
                    builder.append(value ?: "")
                }
            }
            val mac = Mac.getInstance("HmacSHA1")
            val secretKey = SecretKeySpec(applicationProperties.twilioKey.toByteArray(), "HmacSHA1")
            mac.init(secretKey)
            val rawHmac = mac.doFinal(builder.toString().toByteArray(StandardCharsets.UTF_8))
            Base64.getEncoder().encodeToString(rawHmac)
        } catch (e: Exception) {
            null
        }
    }
}