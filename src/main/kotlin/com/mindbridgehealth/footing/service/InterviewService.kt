package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.mindbridgehealth.footing.data.InterviewRepository
import com.mindbridgehealth.footing.data.ScheduledInterviewRepository
import com.mindbridgehealth.footing.data.entity.InterviewEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.beans.ExceptionListener
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull


@Service
class InterviewService(
    private val db: InterviewRepository,
    private val storytellerService: StorytellerService,
    private val chroniclerService: ChroniclerService,
    private val questionService: QuestionService,
    private val interviewQuestionService: InterviewQuestionService,
    private val interviewMapper: InterviewEntityMapper,
    private val storytellerMapper: StorytellerEntityMapper,
    private val interviewQuestionMapper: InterviewQuestionEntityMapper,
    private val chroniclerMapper: ChroniclerEntityMapper,
    private val scheduledInterviewRepository: ScheduledInterviewRepository,
    private val scheduledInterviewEntityMapper: ScheduledInterviewEntityMapper
) {

    fun findInterviewById(interviewId: String): Interview {
        return interviewMapper.entityToModel(db.findById(Base36Encoder.decode(interviewId).toInt()).orElseThrow()) //TODO: Revisit exception
    }

    fun findByStorytellerId(storytellerId: String): Collection<Interview> {
        val storytellerQueryEntity = StorytellerEntity()
        storytellerQueryEntity.id = storytellerId.toInt()
        val interviewResultData = db.findByStorytellerId(storytellerId.toInt())

        return interviewResultData.stream()
            .map { i -> interviewMapper.entityToModel(i) }
            .toList()
    }


    fun createInterview(
        name: String,
        chroniclerId: String,
        storytellerId: String,
        questions: List<String>,
        save: Boolean
    ): Interview {
        val storyteller = storytellerService.findStorytellerById(storytellerId)
            .getOrElse { throw Exception("Storyteller was not found; unable to create interview") }
        val dbChronicler = chroniclerService.findChroniclerById(chroniclerId).getOrNull()
        val dbQuestions = questions
            .filter { qid -> questionService.findQuestionById(qid).isPresent }
            .map { qid ->
                val q = questionService.findQuestionById(qid)
                val iq = InterviewQuestion(null, q.get().name, null, q.get(), null, completed = false, skipped = false)
                interviewQuestionMapper.modelToEntity(interviewQuestionService.save(iq))
            }.toMutableList()

        val chronicler = if(dbChronicler != null) {
            chroniclerMapper.modelToEntity(dbChronicler)
        } else null

        val interview = InterviewEntity(name, null, false, chronicler, storytellerMapper.modelToEntity(storyteller))
        interview.interviewQuestionData = dbQuestions
        if (save) {
            return interviewMapper.entityToModel(db.save(interview))
        }

        return Interview(
            null,
            name,
            emptyList(),
            storyteller,
            dbChronicler,
            null,
            false,
            Collections.emptyList()
        )
    }

    fun scheduleInterview(storytellerId: String, interviewId: String, time: Instant?, name: String?): String {
        //TODO: Can't schedule before onboarding has completed
        val storyteller = storytellerService.findStorytellerById(storytellerId).getOrElse { throw Exception("Storyteller not found") }
        val interview = findInterviewById(interviewId);

        val scheduledTime: Instant = if(time == null ) {
            if((storyteller.preferredTimes?.first()?.time == null || storyteller.preferredTimes?.first()?.dayOfWeek == null)) {
                throw Exception("No specified time or preferred time set")
            }

            val preferredTime = storyteller.preferredTimes?.first()
            val timeOfDay: LocalTime = LocalTime.from(preferredTime?.time?.toLocalTime())
            val now: ZonedDateTime = ZonedDateTime.now()
            val desiredDayOfWeek: DayOfWeek = preferredTime!!.dayOfWeek
            val nextDayOfWeek: ZonedDateTime = now.with(TemporalAdjusters.next(desiredDayOfWeek))
            val nextDateTime: ZonedDateTime = nextDayOfWeek.with(timeOfDay)
            nextDateTime.toInstant()
        } else {
            time
        }

        val scheduledInterview = ScheduledInterview(null, name ?: "unnamed", emptyList(), scheduledTime, interview)

        val existingInterview = scheduledInterviewRepository.findByStorytellerIdAndScheduledTime(
            Base36Encoder.decode(storyteller.id!!).toInt(), Timestamp.from(time)
        )
        if(existingInterview != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Duplicate Scheduled Interview ID: " + Base36Encoder.encode(existingInterview.id.toString()))
        }

        return Base36Encoder.encode(scheduledInterviewRepository.save(scheduledInterviewEntityMapper.modelToEntity(scheduledInterview)).id.toString())
    }

    fun getNextInterview(storytellerId: String): ScheduledInterview? {
        val next = scheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(Base36Encoder.decode(storytellerId).toInt())
        if (next.isEmpty()) {
            return null
        }
        return scheduledInterviewEntityMapper.entityToModel(next.first())
    }

    fun getAllScheduledInterviews(storytellerId: String): Collection<ScheduledInterview> {
        return scheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(Base36Encoder.decode(storytellerId).toInt()).map { s -> scheduledInterviewEntityMapper.entityToModel(s) }
    }

    fun deleteScheduledInterview(scheduledInterviewId: String) {
        return scheduledInterviewRepository.deleteById(Base36Encoder.decode(scheduledInterviewId).toInt())
    }

    //TODO: What happens if you delete an interview that was scheduled?
    fun deleteInterview(id: String) = db.deleteById(Base36Encoder.decode(id).toInt())

}