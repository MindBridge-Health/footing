package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.model.Tag
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import java.util.*
import kotlin.test.assertEquals

class MediaServiceTests {
    private val mockInterviewService = mockk<InterviewQuestionService>()
    private val mockStorytellerService = mockk<StorytellerService>()
    private val mockMediaRepository = mockk<MediaRepository>()
    private val mediaMapper = MediaEntityMapperImpl(StorytellerEntityMapperImpl(
        BenefactorEntityMapperImpl(),
        com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl(),
        PreferredTimeMapperImpl()
    ))
    private val testMedia = Media(null, "My Media", emptyList(), URI.create("http://somewhere.out.there"), "Video", null, null, "recorded")
    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_HappyPath() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)

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
        every { mockStorytellerService.findStorytellerEntityById(any()) } returns Optional.of(storytellerEntity)
        every { mockMediaRepository.save(any()) } returnsArgument 0

        val mediaService = MediaService(mockMediaRepository, mediaMapper, mockStorytellerService, mockInterviewService)
        mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123")

        verify { mockMediaRepository.save(any()) }
    }

    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_QuestionNotFound_Exception() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)

        every { mockInterviewService.findEntityByAltId(any()) } returns Optional.empty()

        val mediaService = MediaService(mockMediaRepository, mediaMapper, mockStorytellerService, mockInterviewService)

        assertThrows<Exception> { mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123") }

    }

    @Test
    fun associateMediaWithStorytellerFromInterviewQuestion_StorytellerNotFound_Exception() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)

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

        val mediaService = MediaService(mockMediaRepository, mediaMapper, mockStorytellerService, mockInterviewService)

        assertThrows<Exception> { mediaService.associateMediaWithStorytellerFromInterviewQuestion(testMedia, "abc123") }

    }

    @Test
    fun updateMediaStatus_validMedia_mediaUpdated() {
        mockInterviewService.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockMediaRepository.clear(MockkClear.BEFORE)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "bdr321"
        }

        every { mockMediaRepository.findByAltId(any()) } returns Optional.of(MediaEntity().apply {
            this.id = 1
            this.altId = "jfu987"
            this.name = "original name"
            this.tags = mutableListOf()
            this.storyteller = storytellerEntity
            this.location ="http://original.location"
            this.state = "completed"
            this.type = "audio"
        })

        val mediaCaptureSlot = CapturingSlot<MediaEntity>()
        every { mockMediaRepository.save(capture(mediaCaptureSlot)) } returnsArgument 0
        val mediaService = MediaService(mockMediaRepository, mediaMapper, mockStorytellerService, mockInterviewService)

        val newMedia = Media("jfu987", "new name", mutableListOf(Tag(null, "test tag", "value")), URI.create("http://new.location"), "video", null, null, "converted")

        mediaService.updateMediaStatus(newMedia, "iq1")
        assertEquals("http://new.location", mediaCaptureSlot.captured.location)
        assertEquals("video", mediaCaptureSlot.captured.type)
        assertEquals("converted", mediaCaptureSlot.captured.state)



    }
}