package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.MediaService
import com.mindbridgehealth.footing.service.util.WebhookSignatureValidator
import jakarta.servlet.ServletException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MediaControllerTests {

    @Autowired
    lateinit var mediaController: MediaController

    @MockBean
    lateinit var mediaService: MediaService

    @Value(value = "\${local.server.port}")
    private val port = 0

    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Autowired
    val mockMvc: MockMvc? = null

    @Autowired
    lateinit var signatureValidator: WebhookSignatureValidator

    val videoRecordedRawJson = "{\n" +
            "    \"version\":\"1.0\",\n" +
            "    \"event\":\"video_recorded\",\n" +
            "    \"data\":{\n" +
            "        \"videoName\":\"STREAM_NAME\",\n" +
            "        \"audioCodec\":\"NellyMoser ASAO\",\n" +
            "        \"videoCodec\":\"H.264\",\n" +
            "        \"type\":\"flv\",\n" +
            "        \"id\":123,\n" +
            "        \"dateTime\":\"2016-03-03 15:51:44\",\n" +
            "        \"timeZone\":\"Europe/Bucharest\",\n" +
            "        \"payload\":\"{\\\"interview_question_id\\\":\\\"i1\\\"}\",\n" +
            "        \"httpReferer\":\"http://site_from_where_video_was_recorded.com\",\n" +
            "        \"cam_name\":\"Logitech HD Pro Webcam C920 (046d:082d)\",\n" +
            "        \"mic_name\":\"Default\",\n" +
            "        \"ip\":\"91.16.93.181\",\n" +
            "        \"ua\":\"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Mobile Safari/537.36\"\n" +
            "    }\n" +
            "}"

    val videoConvetedRawJson = "{\n" +
            "    \"version\":\"1.0\",\n" +
            "    \"event\":\"video_converted\",\n" +
            "    \"data\":{\n" +
            "        \"videoName\":\"STREAM_NAME\",  \n" +
            "        \"duration\":7,\n" +
            "        \"audioCodec\":\"AAC\",\n" +
            "        \"videoCodec\":\"H.264\",\n" +
            "        \"type\":\"mp4\",\n" +
            "        \"size\":194373,\n" +
            "        \"width\":\"320\",\n" +
            "        \"height\":\"240\",\n" +
            "        \"orientation\":\"landscape\",\n" +
            "        \"id\":123,\n" +
            "        \"dateTime\": \"2015-10-10 16:00:36\",\n" +
            "        \"timeZone\":\"Europe/Bucharest\",\n" +
            "        \"payload\":\"{\\\"interview_question_id\\\":\\\"i1\\\"}\"\n" +
            "    }\n" +
            "}"

    val videoCopiedRawJson = "{\n" +
            "  \"version\":\"1.0\",\n" +
            "  \"event\":\"video_copied_pipe_s3\",\n" +
            "  \"data\":{\n" +
            "    \"storedStatus\":\"stored successful\",\n" +
            "    \"videoName\":\"STREAM_NAME\",\n" +
            "    \"size\":194373,\n" +
            "    \"checksum_md5\":\"968302a32f7c7ed67523274aa8a92717\",\n" +
            "    \"checksum_sha1\":\"b733ec235ea57119172c8b044220e793446063fe\",\n" +
            "    \"id\":123,\n" +
            "    \"url\":\"https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.mp4\",\n" +
            "    \"rawRecordingUrl\":\"https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_raw.EXTENSION\",\n" +
            "    \"snapshotUrl\":\"https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.jpg\",\n" +
            "    \"filmstripUrl\":\"https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_filmstrip.jpg\",\n" +
            "    \"cdn\":{\"cdnRecordingUrl\":\"https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.mp4\",\n" +
            "      \"cdnRawRecordingUrl\":\"https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_raw.EXTENSION\",\n" +
            "      \"cdnSnapshotUrl\":\"https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.jpg\",\n" +
            "      \"cdnFilmstripUrl\":\"https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_filmstrip.jpg\"},\n" +
            "    \"bucket\":\"eu1-addpipe\",\n" +
            "    \"region\":\"eu-central-1\",\n" +
            "    \"payload\":\"{\\\"interview_question_id\\\":\\\"i1\\\"}\"\n" +
            "  }\n" +
            "}"

    val unexpectedEventRawJson = "{\n" +
            "    \"version\":\"1.0\",\n" +
            "    \"event\":\"video_copied_dbox\",\n" +
            "    \"data\":{\n" +
            "        \"dboxUploadStatus\":\"upload success\",\n" +
            "        \"videoName\":\"STREAM_NAME\",\n" +
            "        \"type\":\"mp4\",\n" +
            "        \"size\":493534,\n" +
            "        \"checksum_md5\":\"968302a32f7c7ed67523274aa8a92717\",\n" +
            "        \"checksum_sha1\":\"b733ec235ea57119172c8b044220e793446063fe\",\n" +
            "        \"id\":123,\n" +
            "        \"payload\":\"your payload data string\"\n" +
            "    }\n" +
            "}"

    @Test
    fun addPipeCallback_VideoRecordedEvent_MappedCorrectly() {
        val response = mockMvc?.perform(
            post("/api/v1/media/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(videoRecordedRawJson)
                .header(
                    "X-Pipe-Signature",
                    signatureValidator.generateSignature("/api/v1/media/", videoRecordedRawJson)
                )
                .with(SecurityMockMvcRequestPostProcessors.jwt())
        )?.andExpect(status().isOk())?.andReturn()
        assertEquals("videoRecorded", response?.response?.contentAsString)
    }

    @Test
    fun addPipeCallback_VideoConvertedEvent_MappedCorrectly() {
        val response = mockMvc?.perform(
            post("/api/v1/media/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(videoConvetedRawJson)
                .header(
                    "X-Pipe-Signature",
                    signatureValidator.generateSignature("/api/v1/media/", videoConvetedRawJson)
                )
                .with(SecurityMockMvcRequestPostProcessors.jwt())
        )?.andExpect(status().isOk())?.andReturn()
        assertEquals("videoConverted", response?.response?.contentAsString)
    }

    @Test
    fun addPipeCallback_VideoCopiedPipeS3Event_MappedCorrectly() {
        val response = mockMvc?.perform(
            post("/api/v1/media/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(videoCopiedRawJson)
                .header("X-Pipe-Signature", signatureValidator.generateSignature("/api/v1/media/", videoCopiedRawJson))
                .with(SecurityMockMvcRequestPostProcessors.jwt())
        )?.andExpect(status().isOk())?.andReturn()
        assertEquals("videoCopied", response?.response?.contentAsString)
    }

    @Test
    fun addPipeCallback_UnknownEvent_MappedCorrectly() {
        val generatedSignature = signatureValidator.generateSignature("/api/v1/media/", unexpectedEventRawJson)
        assertThrows<ServletException> {
            mockMvc?.perform(
                post("/api/v1/media/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(unexpectedEventRawJson)
                    .header("X-Pipe-Signature", generatedSignature)
                    .with(SecurityMockMvcRequestPostProcessors.jwt())
            )?.andExpect(status().isBadRequest())
        }
    }
}
