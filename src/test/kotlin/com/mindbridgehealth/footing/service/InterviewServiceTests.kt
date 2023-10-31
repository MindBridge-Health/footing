package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.client.TwilioCallService
import com.mindbridgehealth.footing.data.repository.InterviewRepository
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.*
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import java.sql.Time
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InterviewServiceTests {

    private val mockInterviewDb = mockk<InterviewRepository>()
    private val mockStorytellerService = mockk<StorytellerService>()
    private val mockQuestionService = mockk<QuestionService>()
    private val mockInterviewQuestionService = mockk<InterviewQuestionService>()
    private val mockChroniclerService = mockk<ChroniclerService>()
    private val mockScheduledInterviewRepository = mockk<ScheduledInterviewRepository>()
    private val mockTwilioCallService = mockk<TwilioCallService>()

    private val storytellerEntityMapperImpl = StorytellerEntityMapperImpl(
        BenefactorEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())),
        ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())),
        PreferredTimeMapperImpl(),
        OrganizationEntityMapperImpl(),
        UserMapperImpl()
    )

    private val storyEntityMapperImpl = StoryEntityMapperImpl(storytellerEntityMapperImpl)
    private val interviewQuestionEntityMapperImpl = InterviewQuestionEntityMapperImpl(
        com.mindbridgehealth.footing.service.mapper.QuestionEntityMapperImpl(),
        storyEntityMapperImpl
    )
    private val interviewEntityMapper = InterviewEntityMapperImpl(
        TimeMapper(),
        storytellerEntityMapperImpl,
        ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())),
        interviewQuestionEntityMapperImpl
    )

    private val chroniclerEntityMapper = ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl()))

    private val scheduledInterviewEntityMapper = ScheduledInterviewEntityMapperImpl(TimeMapper(), ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), UserMapper(OrganizationEntityMapperImpl())), interviewEntityMapper)

    val service = InterviewService(
        mockInterviewDb,
        mockStorytellerService,
        mockChroniclerService,
        mockQuestionService,
        mockInterviewQuestionService,
        interviewEntityMapper,
        mockScheduledInterviewRepository,
        scheduledInterviewEntityMapper,
        mockTwilioCallService
    )

    @Test
    fun findByStorytellerId_validId_AllStories() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

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
        storyEntity1.originalText = "Once upon a time..."
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
        val preferredChronicler1 = Chronicler(
            Base36Encoder.encodeAltId(chroniclerEntity1.altId!!),
            "Chronicler 1",
            "first",
            "middle",
            "email",
            "mobile",
            true,
            null,
            true
        )
        val storyteller1 = Storyteller(Base36Encoder.encodeAltId(storytellerEntity1.altId!!), "Storyteller 1", "first", "middle","email", "mobile", true, "phone", ArrayList(), preferredChronicler1, OnboardingStatus.ONBOARDING_NOT_STARTED, ArrayList(), null)
        val expectedStory1 = Story(
            Base36Encoder.encodeAltId(storyEntity1.altId!!),
            "Story 1",
            emptyList(),
            "Once upon a time...",
            "Once upon a time...",
            null,
            storyteller1
        )
        val expectedInterviewQuestion1 = InterviewQuestion(Base36Encoder.encodeAltId(interviewQuestionEntity1.altId!!),
            "question 1",
            emptyList(),
            Base36Encoder.encodeAltId(interviewEntity1.altId!!),
            Base36Encoder.encodeAltId(questionEntity.altId!!),
            expectedStory1, completed = true, skipped = false)
        val expectedInterview1 = Interview(Base36Encoder.encodeAltId(interviewEntity1.altId!!), "interview 1", emptyList(), storyteller1, preferredChronicler1, timeCompleted, true, listOf(expectedInterviewQuestion1))

        //Expected Twos
        val preferredChronicler2 = Chronicler(
            Base36Encoder.encodeAltId(chroniclerEntity2.altId!!),
            "Chronicler 2",
            "first",
            "middle",
            "email",
            "mobile",
            false,
            null,
            true,
        )
        val storyteller2 = Storyteller(Base36Encoder.encodeAltId(storytellerEntity2.altId!!), "Storyteller 2", "first", "middle","email", "mobile", true, "phone", ArrayList(), preferredChronicler2, OnboardingStatus.ONBOARDING_NOT_STARTED, ArrayList(), null)
        val expectedInterviewQuestion2 = InterviewQuestion(Base36Encoder.encodeAltId(interviewQuestionEntity2.altId!!),
            "question 2",
            emptyList(),
            Base36Encoder.encodeAltId(interviewEntity2.altId!!),
            Base36Encoder.encodeAltId(questionEntity2.altId!!),
            null, completed = false, skipped = false)
        val expectedInterview2 = Interview(Base36Encoder.encodeAltId(interviewEntity2.altId!!), "interview 2", emptyList(), storyteller2, preferredChronicler2, null, false, listOf(expectedInterviewQuestion2))

        val expectedInterviews = listOf(expectedInterview1, expectedInterview2)

        val service = InterviewService(
            mockInterviewDb,
            mockStorytellerService,
            mockChroniclerService,
            mockQuestionService,
            mockInterviewQuestionService,
            interviewEntityMapper,
            mockScheduledInterviewRepository,
            scheduledInterviewEntityMapper,
            mockTwilioCallService
        )
        val interviews = service.findByStorytellerId(uuid1.toString())

        val actualInterviews = ArrayList(interviews)
        assertEquals(expectedInterviews, actualInterviews)

    }

    @Test
    fun createInterview_validData_interviewCreated() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val service = InterviewService(
            mockInterviewDb,
            mockStorytellerService,
            mockChroniclerService,
            mockQuestionService,
            mockInterviewQuestionService,
            interviewEntityMapper,
            mockScheduledInterviewRepository,
            scheduledInterviewEntityMapper,
            mockTwilioCallService
        )

        val name = "interview 1"
        val chroniclerId = "c1"
        val storytellerId = "s1"
        val qIds = listOf("q1")

        val interviewEntitySlot = slot<InterviewEntity>()
        every { mockInterviewDb.save(capture(interviewEntitySlot))} answers { interviewEntitySlot.captured.apply {
            this.id = 1
            this.altId = "i1"
        }}
        val chroniclerEntity = ChroniclerEntity().apply {
            this.id = 1
            this.altId = "c1"
            this.lastname = "ln"
            this.firstname = "fn"
            this.middlename = "mn"
            this.email = "em"
            this.mobile = "87"
            this.ai = true
        }
        every { mockChroniclerService.findChroniclerEntityByAltId(any()) } returns Optional.of(chroniclerEntity)
        val questionEntity = QuestionEntity().apply {
            this.id = 1
            this.altId = "q1"
            this.name = "question 1"
            this.tags = mutableListOf()
            this.text = "text?"
            this.isCustom = false
        }
        every { mockQuestionService.findQuestionEntityByAltId(any()) } returns Optional.of(questionEntity)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
        }
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } returns Optional.of(storytellerEntity)
        val interviewQuestionEntitySlot = slot<InterviewQuestionEntity>()
        every { mockInterviewQuestionService.save(capture(interviewQuestionEntitySlot)) } answers { interviewQuestionEntitySlot.captured.apply {
            this.id = 1
            this.altId = "iq1"
        }}

        val interview = service.createInterview(name, chroniclerId, storytellerId, qIds)

        assertEquals(name, interview.name)
        assertEquals(false, interview.completed)
        assertEquals(Chronicler("dlj", "ln", "fn", "mn", "em", "87", true, null, true), interview.chronicler)
        assertEquals(Storyteller("5rm","ln", "fn", null, "em", "98", true, "text", emptyList(), null, OnboardingStatus.ONBOARDING_NOT_STARTED, emptyList(), null), interview.storyteller)
        assertEquals(InterviewQuestion("lz344", "interview 1_question 1", emptyList(), "1sk", "xcm", null, false, false), interview.interviewQuestions?.first())
    }

    @Test
    fun scheduleInterview_now_success() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns null

        val savedScheduledInterviewSlot = slot<ScheduledInterviewEntity>()
        every { mockScheduledInterviewRepository.save(capture(savedScheduledInterviewSlot)) } answers  {
            savedScheduledInterviewSlot.captured.id = 1
            savedScheduledInterviewSlot.captured.altId = "i1"
            savedScheduledInterviewSlot.captured
        }

        val scheduledInterviewId = service.scheduleInterview("1", "1", nowInstant, "scIn1", true)

        assertEquals(1, savedScheduledInterviewSlot.captured.id)
        assertEquals("i1", savedScheduledInterviewSlot.captured.altId)
        assertEquals("scIn1", savedScheduledInterviewSlot.captured.name)
        assertEquals(Timestamp.from(Instant.ofEpochMilli(now.time)), savedScheduledInterviewSlot.captured.scheduledTime)

    }

    @Test
    fun scheduleInterview_nextPreferredTime_success() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns null
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf()

        val savedScheduledInterviewSlot = slot<ScheduledInterviewEntity>()
        every { mockScheduledInterviewRepository.save(capture(savedScheduledInterviewSlot)) } answers  {
            savedScheduledInterviewSlot.captured.id = 1
            savedScheduledInterviewSlot.captured.altId = "i1"
            savedScheduledInterviewSlot.captured
        }


        val scheduledInterviewId = service.scheduleInterview("1", "1", null, "scIn1", true)

        assertEquals(1, savedScheduledInterviewSlot.captured.id)
        assertEquals("i1", savedScheduledInterviewSlot.captured.altId)
        assertEquals("scIn1", savedScheduledInterviewSlot.captured.name)
        assertEquals(getNextPreferredTime(now.toLocalTime(), DayOfWeek.WEDNESDAY), savedScheduledInterviewSlot.captured.scheduledTime)

    }

    @Test
    fun scheduleInterview_nextPreferredTimeAfterScheduled_success() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        val nextWednesday = getNextPreferredTime(now.toLocalTime(), DayOfWeek.WEDNESDAY)
        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns null
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf(ScheduledInterviewEntity().apply {
            this.id = 1
            this.altId = "si1"
            this.scheduledTime = nextWednesday
        })

        val savedScheduledInterviewSlot = slot<ScheduledInterviewEntity>()
        every { mockScheduledInterviewRepository.save(capture(savedScheduledInterviewSlot)) } answers  {
            savedScheduledInterviewSlot.captured.id = 1
            savedScheduledInterviewSlot.captured.altId = "i1"
            savedScheduledInterviewSlot.captured
        }


        val scheduledInterviewId = service.scheduleInterview("1", "1", null, "scIn1", true)

        assertEquals(1, savedScheduledInterviewSlot.captured.id)
        assertEquals("i1", savedScheduledInterviewSlot.captured.altId)
        assertEquals("scIn1", savedScheduledInterviewSlot.captured.name)
        assertEquals(getNextPreferredTime(now.toLocalTime(), DayOfWeek.WEDNESDAY, 1), savedScheduledInterviewSlot.captured.scheduledTime)

    }

    @Test
    fun scheduleInterview_nextPreferredTimeNoAppend_conflictError() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        val nextWednesday = getNextPreferredTime(now.toLocalTime(), DayOfWeek.WEDNESDAY)
        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns ScheduledInterviewEntity()
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf(ScheduledInterviewEntity().apply {
            this.id = 1
            this.altId = "si1"
            this.scheduledTime = nextWednesday
        })

        val savedScheduledInterviewSlot = slot<ScheduledInterviewEntity>()
        every { mockScheduledInterviewRepository.save(capture(savedScheduledInterviewSlot)) } answers  {
            savedScheduledInterviewSlot.captured.id = 1
            savedScheduledInterviewSlot.captured.altId = "i1"
            savedScheduledInterviewSlot.captured
        }


        assertThrows<ResponseStatusException> { service.scheduleInterview("1", "1", null, "scIn1", false) }

    }

    @Test
    fun getAllScheduledInterviews_oldInterview_excluded() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        val nextDayOfWeek: ZonedDateTime = ZonedDateTime.now().plusWeeks(-2).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY))
        val lastWednesday = Timestamp.from(nextDayOfWeek.with(now.toLocalTime()).toInstant())
        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns ScheduledInterviewEntity()
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf(ScheduledInterviewEntity().apply {
            this.id = 1
            this.name = "old interview"
            this.altId = "si1"
            this.scheduledTime = lastWednesday
            this.interview = InterviewEntity("name", "iid1", null, false, null, storytellerEntity)
        })

        assertEquals(0, service.getAllScheduledInterviews("s1").size)

    }

    @Test
    fun getNextInterview_onlyOldInterview_null() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        val nextDayOfWeek: ZonedDateTime = ZonedDateTime.now().plusWeeks(-1).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY))
        val lastWednesday = Timestamp.from(nextDayOfWeek.with(now.toLocalTime()).toInstant())
        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns ScheduledInterviewEntity()
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf(ScheduledInterviewEntity().apply {
            this.id = 1
            this.name = "old interview"
            this.altId = "si1"
            this.scheduledTime = lastWednesday
            this.interview = InterviewEntity("name", "iid1", null, false, null, storytellerEntity)
        })

        assertNull(service.getNextInterview("s1"))

    }

    @Test //This isn't a great test as we rely on the DB for ordering of interviews
    fun getNextInterview_multipleInterviews_soonest() {
        mockInterviewDb.clear(MockkClear.BEFORE)
        mockStorytellerService.clear(MockkClear.BEFORE)
        mockQuestionService.clear(MockkClear.BEFORE)
        mockInterviewQuestionService.clear(MockkClear.BEFORE)
        mockChroniclerService.clear(MockkClear.BEFORE)
        mockScheduledInterviewRepository.clear(MockkClear.BEFORE)

        val nowInstant = Instant.ofEpochSecond(Instant.now().epochSecond) //All this cuz we don't care about millis
        val now = Time(nowInstant.epochSecond * 1000)
        val storytellerEntity = StorytellerEntity().apply {
            this.id = 1
            this.altId = "s1"
            this.firstname = "fn"
            this.lastname = "ln"
            this.email = "em"
            this.mobile = "98"
            this.contactMethod = "text"
            this.preferredTimes = mutableListOf(PreferredTimeEntity().apply {
                this.id = 1
                this.time = now
                this.dayOfWeek = "WEDNESDAY"
            })
        }

        val nextDayOfWeek: ZonedDateTime = ZonedDateTime.now().plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY))
        val nextWednesday = Timestamp.from(nextDayOfWeek.with(now.toLocalTime()).toInstant())


        every { mockInterviewDb.findByAltId(any()) } answers  { Optional.of(InterviewEntity("Interview1", "i1", null, false, null, storytellerEntity))}
        every { mockStorytellerService.findStorytellerEntityByAltId(any()) } answers { Optional.of(storytellerEntity) }
        every { mockScheduledInterviewRepository.findByStorytellerIdAndScheduledTime(any(), any()) } returns ScheduledInterviewEntity()
        every { mockScheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(any()) } returns listOf(
            ScheduledInterviewEntity().apply {
                this.id = 2
                this.name = "soonest interview"
                this.altId = "si2"
                this.scheduledTime = Timestamp.from(Instant.now().plusSeconds(10))
                this.interview = InterviewEntity("name", "iid2", null, false, null, storytellerEntity)
            },
            ScheduledInterviewEntity().apply {
            this.id = 1
            this.name = "interview"
            this.altId = "si1"
            this.scheduledTime = nextWednesday
            this.interview = InterviewEntity("name", "iid1", null, false, null, storytellerEntity)
        })

        val nextInterview = service.getNextInterview("s1")
        assertNotNull(nextInterview)
        assertEquals("soonest interview", nextInterview.name)

    }

    private fun getNextPreferredTime(timeOfDay: LocalTime, dayOfWeek: DayOfWeek, weeksToSkip: Long = 0): Timestamp {
        val now: ZonedDateTime = ZonedDateTime.now()
        val nextDayOfWeek: ZonedDateTime = now.plusWeeks(weeksToSkip).with(TemporalAdjusters.next(dayOfWeek))
        return Timestamp.from(nextDayOfWeek.with(timeOfDay).toInstant())
    }
}