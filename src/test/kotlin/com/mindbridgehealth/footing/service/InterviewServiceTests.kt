package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.InterviewRepository
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.data.entity.*
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import io.mockk.every
import io.mockk.mockk
import org.mapstruct.factory.Mappers
import java.sql.Timestamp
import java.time.Instant
import kotlin.math.floor
import kotlin.test.assertEquals

class InterviewServiceTests {


    //@Test
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
        questionEntity.name = "question 1"
        questionEntity.isCustom = false
        questionEntity.text = "What is your favorite color?"

        val chroniclerEntity1 = ChroniclerEntity()
        chroniclerEntity1.id = uuid1
        chroniclerEntity1.lastname = "Chronicler 1"
        chroniclerEntity1.firstname = "first"
        chroniclerEntity1.middlename = "middle"
        chroniclerEntity1.ai = true

        val storytellerEntity1 = StorytellerEntity()
        storytellerEntity1.id = uuid1
        storytellerEntity1.lastname = "Storyteller 1"
        storytellerEntity1.firstname = "first"
        storytellerEntity1.middlename = "middle"
        storytellerEntity1.benefactors = mutableListOf()
        storytellerEntity1.contactMethod = "phone"
        storytellerEntity1.preferredChronicler = chroniclerEntity1

        val storyEntity1 = StoryEntity()
        storyEntity1.id = uuid1
        storyEntity1.name = "Story 1"
        storyEntity1.text = "Once upon a time..."
        storyEntity1.storyteller = storytellerEntity1

        val interviewQuestionEntity1 = InterviewQuestionEntity()
        interviewQuestionEntity1.id = uuid1
        interviewQuestionEntity1.name = "question 1"
        interviewQuestionEntity1.question = questionEntity
        interviewQuestionEntity1.completed = true
        interviewQuestionEntity1.skipped = false
        interviewQuestionEntity1.story = storyEntity1

        val timeCompleted = Instant.now()
        val interviewEntity1 = InterviewEntity("interview 1", Timestamp.from(timeCompleted), true, chroniclerEntity1, storytellerEntity1)
        interviewEntity1.id = uuid1
        interviewEntity1.interviewQuestionData?.add(interviewQuestionEntity1)

        //Twos
        val uuid2 = floor(Math.random() * 1000).toInt()
        val questionEntity2 = QuestionEntity()
        questionEntity2.id = uuid2
        questionEntity2.name = "question 2"
        questionEntity2.isCustom = true
        questionEntity2.text = "What is your favorite book?"

        val chroniclerEntity2 = ChroniclerEntity()
        chroniclerEntity2.id = uuid2
        chroniclerEntity2.lastname = "Chronicler 2"
        chroniclerEntity2.firstname = "first"
        chroniclerEntity2.middlename = "middle"
        chroniclerEntity2.ai = false

        val storytellerEntity2 = StorytellerEntity()
        storytellerEntity2.id = uuid2
        storytellerEntity2.lastname = "Storyteller 2"
        storytellerEntity2.firstname = "first"
        storytellerEntity2.middlename = "middle"
        storytellerEntity2.benefactors = mutableListOf()
        storytellerEntity2.contactMethod = "phone"
        storytellerEntity2.preferredChronicler = chroniclerEntity2

        val storyEntity2 = StoryEntity()
        storyEntity2.id = uuid2
        storyEntity2.name = "Story 2"
        storyEntity1.text = "Once upon a time..."
        storyEntity2.storyteller = storytellerEntity2

        val interviewQuestionEntity2 = InterviewQuestionEntity()
        interviewQuestionEntity2.question = questionEntity2
        interviewQuestionEntity2.completed = false
        interviewQuestionEntity2.skipped = false


        val interviewEntity2 = InterviewEntity("interview 2", null, false, chroniclerEntity2, storytellerEntity2)
        interviewEntity2.id = uuid2
        interviewEntity2.interviewQuestionData?.add(interviewQuestionEntity2)

        //Result
        val interviewResultList = ArrayList<InterviewEntity>(2)
        interviewResultList.add(interviewEntity1)
        interviewResultList.add(interviewEntity2)

        //Mock
        every {mockInterviewDb.findByStorytellerId(any())} returns interviewResultList

        //Expected Ones
        val expectedQuestion1 = Question(uuid1.toString(), "question 1", emptyList(), "What is your favorite color?", false)
        val preferredChronicler1 = Chronicler(uuid1.toString(), "Chronicler 1","first", "middle", "", "", true)
        val storyteller1 = Storyteller(uuid1.toString(), "Storyteller 1", "first", "middle","", "", "phone", ArrayList(), preferredChronicler1, OnboardingStatus.ONBOARDING_NOT_STARTED, null)
        val expectedStory1 = Story(uuid1.toString(), "Story 1", emptyList(), "Once upon a time...", storyteller1)
        val expectedInterviewQuestion1 = InterviewQuestion(uuid1.toString(), "question 1", emptyList(), expectedQuestion1, expectedStory1, completed = true, skipped = false)
        val expectedInterview1 = Interview(uuid1.toString(), "interview 1", emptyList(), storyteller1, preferredChronicler1, timeCompleted, true, listOf(expectedInterviewQuestion1))

        //Expected Twos
        val expectedQuestion2 = Question(uuid2.toString(), "question 2", emptyList(), "What is your favorite book?", false)
        val preferredChronicler2 = Chronicler(uuid2.toString(), "Chronicler 2","first", "middle","",  "", false)
        val storyteller2 = Storyteller(uuid2.toString(), "Storyteller 2", "first", "middle","", "", "phone", ArrayList(), preferredChronicler2, OnboardingStatus.ONBOARDING_NOT_STARTED, null)
        val expectedStory2 = Story(uuid2.toString(), "Story 2", emptyList(), "Once upon a time...", storyteller2)
        val expectedInterviewQuestion2 = InterviewQuestion(uuid2.toString(), "question 2", emptyList(), expectedQuestion2, expectedStory2, completed = true, skipped = false)
        val expectedInterview2 = Interview(uuid2.toString(), "interview 2", emptyList(), storyteller2, preferredChronicler2, null, false, listOf(expectedInterviewQuestion2))

        val expectedInterviews = listOf(expectedInterview1, expectedInterview2)

        val service = InterviewService(
            mockInterviewDb,
            mockStorytellerDb,
            mockChroniclerService,
            mockQuestionService,
            mockInterviewQuestionService,
            Mappers.getMapper(InterviewEntityMapper::class.java),
            Mappers.getMapper(StorytellerEntityMapper::class.java),
            Mappers.getMapper(InterviewQuestionEntityMapper::class.java),
            Mappers.getMapper(ChroniclerEntityMapper::class.java),
            mockScheduledInterviewRepository,
            Mappers.getMapper(ScheduledInterviewEntityMapper::class.java)

        )
        val interviews = service.findByStorytellerId(uuid1.toString())

        val actualInterviews = ArrayList(interviews)
        assertEquals(expectedInterviews, actualInterviews)

    }
}