package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.api.dto.addpipe.*
import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.data.repository.MediaStatusRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.model.Tag
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.test.assertEquals

class MediaServiceTests {
    private val mockInterviewService = mockk<InterviewQuestionService>()
    private val mockStorytellerService = mockk<StorytellerService>()
    private val mockMediaRepository = mockk<MediaRepository>()
    private val mockMediaStatusRepository = mockk<MediaStatusRepository>()
    private val mockStoryService = mockk<StoryService>()
    private val mockSmsNotificationService = mockk<SmsNotificationService>()
    val storytellerEntityMapperImpl = StorytellerEntityMapperImpl(
        BenefactorEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())),
        ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())),
        PreferredTimeMapperImpl(),
        OrganizationEntityMapperImpl(),
        UserMapperImpl()
    )
    private val mediaMapper = MediaEntityMapperImpl(
        storytellerEntityMapperImpl,
        StoryEntityMapperImpl(storytellerEntityMapperImpl)
    )
    private val testMedia =
        Media(null, "My Media", emptyList(), URI.create("http://somewhere.out.there"), "Video", null, null)

    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_HappyPath() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)
        mockMediaStatusRepository.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)

        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "bdr321"
            this.mobile = "1231231234"
        }

        val interview = InterviewEntity().apply {
            this.id = 1
            this.altId = "dcf321"
            this.name = "first interview"
            this.storyteller = storytellerEntity
        }

        val interviewQuestionEntity = InterviewQuestionEntity().apply {
            this.id = 1
            this.altId = "abc123"
            this.name = "test IQ 1"
            this.completed = true
            this.skipped = false
            this.interview = interview
        }
        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.of(interviewQuestionEntity)
        every { mockStorytellerService.findStorytellerEntityById(any()) } returns Optional.of(storytellerEntity)
        every { mockMediaRepository.save(any()) } returnsArgument 0
        every { mockMediaStatusRepository.save(any()) } returnsArgument 0
        every { mockStoryService.saveEntity(any()) } returnsArgument 0
        every { mockSmsNotificationService.sendMessage(any(), any()) } returns Unit

        val mediaService = MediaService(
            mockMediaRepository,
            mockMediaStatusRepository,
            mediaMapper,
            mockStorytellerService,
            mockInterviewService,
            mockStoryService,
            mockSmsNotificationService,
        )
        mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123", MediaStatusEntity())

        verify { mockMediaRepository.save(any()) }
        verify { mockSmsNotificationService.sendMessage(any(), any()) }
    }

    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_QuestionNotFound_Exception() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)
        mockMediaStatusRepository.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)

        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.empty()
        every { mockMediaStatusRepository.save(any()) } returnsArgument 0
        every { mockSmsNotificationService.sendMessage(any(), any()) } returns Unit

        val mediaService = MediaService(
            mockMediaRepository,
            mockMediaStatusRepository,
            mediaMapper,
            mockStorytellerService,
            mockInterviewService,
            mockStoryService,
            mockSmsNotificationService,
        )

        assertThrows<Exception> { mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123", MediaStatusEntity()) }
        verify { mockSmsNotificationService wasNot called }
    }

    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_StorytellerNotFound_Exception() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)
        mockMediaStatusRepository.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)

        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "bdr321"
        }

        val interview = InterviewEntity().apply {
            this.id = 1
            this.altId = "dcf321"
            this.name = "first interview"
            this.storyteller = storytellerEntity
        }

        val interviewQuestionEntity = InterviewQuestionEntity().apply {
            this.id = 1
            this.altId = "abc123"
            this.name = "test IQ 1"
            this.completed = true
            this.skipped = false
            this.interview = interview
        }
        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.of(interviewQuestionEntity)
        every { mockStorytellerService.findStorytellerEntityById(any()) } returns Optional.empty()
        every { mockMediaStatusRepository.save(any()) } returnsArgument 0
        every { mockSmsNotificationService.sendMessage(any(), any()) } returns Unit

        val mediaService = MediaService(
            mockMediaRepository,
            mockMediaStatusRepository,
            mediaMapper,
            mockStorytellerService,
            mockInterviewService,
            mockStoryService,
            mockSmsNotificationService,
        )

        assertThrows<Exception> { mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123", MediaStatusEntity()) }
        verify { mockSmsNotificationService wasNot called }
    }

    @Test
    fun updateMediaStatus_validMedia_mediaUpdated() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)
        mockMediaStatusRepository.clear(MockkClear.BEFORE)
        mockStoryService.clear(MockkClear.BEFORE)
        mockSmsNotificationService.clear(MockkClear.BEFORE)

        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "bdr321"
            this.mobile = "1231231234"
        }

        every { mockMediaRepository.findByAltId(any()) } returns Optional.of(MediaEntity().apply {
            this.id = 1
            this.altId = "jfu987"
            this.name = "original name"
            this.tags = mutableListOf()
            this.storyteller = storytellerEntity
            this.location = "http://original.location"
            this.type = "audio"
        })

        val mediaCaptureSlot = CapturingSlot<MediaEntity>()
        every { mockMediaRepository.save(capture(mediaCaptureSlot)) } returnsArgument 0
        every { mockMediaStatusRepository.save(any()) } returnsArgument 0
        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.of(InterviewQuestionEntity().apply {
            this.id = 1; this.altId = "iq1"; this.interview =
            InterviewEntity().apply { this.storyteller = storytellerEntity }
        })
        every { mockStoryService.saveEntity(any()) } returnsArgument 0
        every { mockSmsNotificationService.sendMessage(any(), any()) } returns Unit
        val mediaService = MediaService(
            mockMediaRepository,
            mockMediaStatusRepository,
            mediaMapper,
            mockStorytellerService,
            mockInterviewService,
            mockStoryService,
            mockSmsNotificationService,
        )

        val newMedia = Media(
            "jfu987",
            "new name",
            mutableListOf(Tag(null, "test tag", "value")),
            URI.create("http://new.location"),
            "video",
            null,
            null
        )
        val storedStatus = "stored successful"
        val videoName = "example.mp4"
        val size = "1024 MB"
        val checksumMd5 = "968302a32f7c7ed67523274aa8a92717"
        val checksumSha1 = "b733ec235ea57119172c8b044220e793446063fe"
        val id = "123456"
        val url = URI("https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.mp4").toURL()
        val rawRecordingUrl: URL? =
            URI("https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_raw.EXTENSION").toURL()
        val snapshotUrl: URL? =
            URI("https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.jpg").toURL()
        val filmstripUrl: URL? =
            URI("https://addpipevideos.s3.amazonaws.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_filmstrip.jpg").toURL()
        val cdn = CdnData(
            cdnRecordingUrl = URI("https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.mp4").toURL(),
            cdnRawRecordingUrl = URI("https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_raw.EXTENSION").toURL(),
            cdnSnapshotUrl = URI("https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME.jpg").toURL(),
            cdnFilmstripUrl = URI("https://recordings-eu.addpipe.com/b8e2f5bfd04a93b434bd8c740bff744d/STREAM_NAME_filmstrip.jpg").toURL()
        )
        val bucket = "eu1-addpipe"
        val region = "eu-central-1"
        val payload = "your payload data"

        val videoCopiedPipeS3EventData = VideoCopiedPipeS3EventData(
            storedStatus = storedStatus,
            videoName = videoName,
            size = size,
            checksumMd5 = checksumMd5,
            checksumSha1 = checksumSha1,
            id = id,
            url = url,
            rawRecordingUrl = rawRecordingUrl,
            snapshotUrl = snapshotUrl,
            filmstripUrl = filmstripUrl,
            cdn = cdn,
            bucket = bucket,
            region = region,
            payload = payload
        )

        mediaService.updateMediaStatus(
            newMedia, "iq1", VideoCopiedPipeS3Event(
                "v1", videoCopiedPipeS3EventData
            )
        )
        assertEquals("http://new.location", mediaCaptureSlot.captured.location)
        assertEquals("video", mediaCaptureSlot.captured.type)
        verify { mockSmsNotificationService.sendMessage(any(), any()) }
//        assertEquals("converted", mediaCaptureSlot.captured.state)


    }
}