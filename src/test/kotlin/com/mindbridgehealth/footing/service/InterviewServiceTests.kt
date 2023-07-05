package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.InterviewRepository
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.math.floor
import kotlin.test.assertEquals

class InterviewServiceTests {


    @Test
    fun findByStorytellerId_validId_AllStories() {
        val mockInterviewDb = mockk<InterviewRepository>()
        val mockStorytellerDb = mockk<StorytellerService>()
        val mockQuestionService = mockk<QuestionService>()
        val mockInterviewQuestionService = mockk<InterviewQuestionService>()
        val mockChroniclerService = mockk<ChroniclerService>()
        val mockScheduledInterviewRepository = mockk<ScheduledInterviewRepository>()

        //Ones
        val uuid1 = floor(Math.random() * 1000).toInt()
        val questionEntity = QuestionEntity()
        questionEntity.id = uuid1
        questionEntity.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        questionEntity.name = "question 1"
        questionEntity.isCustom = false
        questionEntity.text = "What is your favorite color?"

        val chroniclerEntity1 = ChroniclerEntity()
        chroniclerEntity1.id = uuid1
        chroniclerEntity1.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        chroniclerEntity1.lastname = "Chronicler 1"
        chroniclerEntity1.firstname = "first"
        chroniclerEntity1.middlename = "middle"
        chroniclerEntity1.ai = true
        chroniclerEntity1.email = "email"
        chroniclerEntity1.mobile = "mobile"

        val storytellerEntity1 = StorytellerEntity()
        storytellerEntity1.id = uuid1
        storytellerEntity1.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        storytellerEntity1.lastname = "Storyteller 1"
        storytellerEntity1.firstname = "first"
        storytellerEntity1.middlename = "middle"
        storytellerEntity1.benefactors = mutableListOf()
        storytellerEntity1.contactMethod = "phone"
        storytellerEntity1.preferredChronicler = chroniclerEntity1
        storytellerEntity1.email = "email"
        storytellerEntity1.mobile = "mobile"

        val storyEntity1 = StoryEntity()
        storyEntity1.id = uuid1
        storyEntity1.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        storyEntity1.name = "Story 1"
        storyEntity1.text = "Once upon a time..."
        storyEntity1.storyteller = storytellerEntity1

        val interviewQuestionEntity1 = InterviewQuestionEntity()
        interviewQuestionEntity1.id = uuid1
        interviewQuestionEntity1.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        interviewQuestionEntity1.name = "question 1"
        interviewQuestionEntity1.question = questionEntity
        interviewQuestionEntity1.completed = true
        interviewQuestionEntity1.skipped = false
        interviewQuestionEntity1.story = storyEntity1

        val timeCompleted = Instant.now()
        val interviewEntity1 = InterviewEntity("interview 1", null, Timestamp.from(timeCompleted), true, chroniclerEntity1, storytellerEntity1)
        interviewEntity1.id = uuid1
        interviewEntity1.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        interviewEntity1.interviewQuestionData = mutableListOf(interviewQuestionEntity1)

        //Twos
        val uuid2 = floor(Math.random() * 1000).toInt()
        val questionEntity2 = QuestionEntity()
        questionEntity2.id = uuid2
        questionEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        questionEntity2.name = "question 2"
        questionEntity2.isCustom = true
        questionEntity2.text = "What is your favorite book?"

        val chroniclerEntity2 = ChroniclerEntity()
        chroniclerEntity2.id = uuid2
        chroniclerEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        chroniclerEntity2.lastname = "Chronicler 2"
        chroniclerEntity2.firstname = "first"
        chroniclerEntity2.middlename = "middle"
        chroniclerEntity2.ai = false
        chroniclerEntity2.email = "email"
        chroniclerEntity2.mobile = "mobile"

        val storytellerEntity2 = StorytellerEntity()
        storytellerEntity2.id = uuid2
        storytellerEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        storytellerEntity2.lastname = "Storyteller 2"
        storytellerEntity2.firstname = "first"
        storytellerEntity2.middlename = "middle"
        storytellerEntity2.benefactors = mutableListOf()
        storytellerEntity2.contactMethod = "phone"
        storytellerEntity2.preferredChronicler = chroniclerEntity2
        storytellerEntity2.email = "email"
        storytellerEntity2.mobile = "mobile"

        val storyEntity2 = StoryEntity()
        storyEntity2.id = uuid2
        storyEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        storyEntity2.name = "Story 2"
        storyEntity1.text = "Once upon a time..."
        storyEntity2.storyteller = storytellerEntity2

        val interviewQuestionEntity2 = InterviewQuestionEntity()
        interviewQuestionEntity2.id = uuid2
        interviewQuestionEntity2.name = "question 2"
        interviewQuestionEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        interviewQuestionEntity2.question = questionEntity2
        interviewQuestionEntity2.completed = false
        interviewQuestionEntity2.skipped = false


        val interviewEntity2 = InterviewEntity("interview 2", null, null, false, chroniclerEntity2, storytellerEntity2)
        interviewEntity2.id = uuid2
        interviewEntity2.altId = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        interviewEntity2.interviewQuestionData = mutableListOf(interviewQuestionEntity2)

        //Result
        val interviewResultList = ArrayList<InterviewEntity>(2)
        interviewResultList.add(interviewEntity1)
        interviewResultList.add(interviewEntity2)

        //Mock
        every {mockInterviewDb.findByStorytellerId(any())} returns interviewResultList

        //Expected Ones
        val expectedQuestion1 = Question(Base36Encoder.encodeAltId(questionEntity.altId!!), "question 1", emptyList(), "What is your favorite color?", false)
        val preferredChronicler1 = Chronicler(Base36Encoder.encodeAltId(chroniclerEntity1.altId!!), "Chronicler 1","first", "middle", "email", "mobile", true)
        val storyteller1 = Storyteller(Base36Encoder.encodeAltId(storytellerEntity1.altId!!), "Storyteller 1", "first", "middle","email", "mobile", "phone", ArrayList(), preferredChronicler1, OnboardingStatus.ONBOARDING_NOT_STARTED, ArrayList())
        val expectedStory1 = Story(Base36Encoder.encodeAltId(storyEntity1.altId!!), "Story 1", emptyList(), "Once upon a time...", storyteller1)
        val expectedInterviewQuestion1 = InterviewQuestion(Base36Encoder.encodeAltId(interviewQuestionEntity1.altId!!),
            "question 1",
            emptyList(),
            Base36Encoder.encodeAltId(interviewEntity1.altId!!),
            Base36Encoder.encodeAltId(questionEntity.altId!!),
            expectedStory1, completed = true, skipped = false)
        val expectedInterview1 = Interview(Base36Encoder.encodeAltId(interviewEntity1.altId!!), "interview 1", emptyList(), storyteller1, preferredChronicler1, timeCompleted, true, listOf(expectedInterviewQuestion1))

        //Expected Twos
        val expectedQuestion2 = Question(Base36Encoder.encodeAltId(questionEntity2.altId!!), "question 2", emptyList(), "What is your favorite book?", false)
        val preferredChronicler2 = Chronicler(Base36Encoder.encodeAltId(chroniclerEntity2.altId!!), "Chronicler 2","first", "middle","email",  "mobile", false)
        val storyteller2 = Storyteller(Base36Encoder.encodeAltId(storytellerEntity2.altId!!), "Storyteller 2", "first", "middle","email", "mobile", "phone", ArrayList(), preferredChronicler2, OnboardingStatus.ONBOARDING_NOT_STARTED, ArrayList())
        val expectedStory2 = Story(Base36Encoder.encodeAltId(storyEntity2.altId!!), "Story 2", emptyList(), "Once upon a time...", storyteller2)
        val expectedInterviewQuestion2 = InterviewQuestion(Base36Encoder.encodeAltId(interviewQuestionEntity2.altId!!),
            "question 2",
            emptyList(),
            Base36Encoder.encodeAltId(interviewEntity2.altId!!),
            Base36Encoder.encodeAltId(questionEntity2.altId!!),
            null, completed = false, skipped = false)
        val expectedInterview2 = Interview(Base36Encoder.encodeAltId(interviewEntity2.altId!!), "interview 2", emptyList(), storyteller2, preferredChronicler2, null, false, listOf(expectedInterviewQuestion2))

        val expectedInterviews = listOf(expectedInterview1, expectedInterview2)

        val storytellerEntityMapperImpl = StorytellerEntityMapperImpl(
            BenefactorEntityMapperImpl(),
            com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl(),
            PreferredTimeMapperImpl()
        )
        val interviewQuestionEntityMapperImpl = InterviewQuestionEntityMapperImpl(
            storytellerEntityMapperImpl,
            com.mindbridgehealth.footing.service.mapper.QuestionEntityMapperImpl()
        )
        val iem = InterviewEntityMapperImpl(
            TimeMapper(),
            storytellerEntityMapperImpl,
            com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl(),
            interviewQuestionEntityMapperImpl
        )

        val service = InterviewService(
            mockInterviewDb,
            mockStorytellerDb,
            mockChroniclerService,
            mockQuestionService,
            mockInterviewQuestionService,
            iem,
            storytellerEntityMapperImpl,
            interviewQuestionEntityMapperImpl,
            Mappers.getMapper(ChroniclerEntityMapper::class.java),
            mockScheduledInterviewRepository,
            Mappers.getMapper(ScheduledInterviewEntityMapper::class.java)

        )
        val interviews = service.findByStorytellerId(uuid1.toString())

        val actualInterviews = ArrayList(interviews)
        assertEquals(expectedInterviews, actualInterviews)

    }
}