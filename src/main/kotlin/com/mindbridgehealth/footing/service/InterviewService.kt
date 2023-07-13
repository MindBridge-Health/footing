package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.InterviewRepository
import com.mindbridgehealth.footing.data.repository.ScheduledInterviewRepository
import com.mindbridgehealth.footing.service.entity.InterviewEntity
import com.mindbridgehealth.footing.service.entity.InterviewQuestionEntity
import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatus

import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.sql.Timestamp
import java.time.*
import java.time.temporal.TemporalAdjusters
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
    private val scheduledInterviewRepository: ScheduledInterviewRepository,
    private val scheduledInterviewEntityMapper: ScheduledInterviewEntityMapper
) {

    fun findInterviewById(interviewId: String): Interview {
        return interviewMapper.entityToModel(db.findByAltId(interviewId).orElseThrow()) //TODO: Footing-2 Revisit exception
    }

    fun findInterviewEntityByAltId(interviewId: String): InterviewEntity {
        return db.findByAltId(interviewId).orElseThrow() //TODO: Footing-2 Revisit exception
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
        questionIds: List<String>?
    ): Interview {
        val storyteller = storytellerService.findStorytellerEntityByAltId(storytellerId)
            .getOrElse { throw Exception("Storyteller was not found; unable to create interview") }

        val chronicler = chroniclerService.findChroniclerEntityByAltId(chroniclerId)
            .getOrElse { throw Exception("Chronicler was not found; unable to create interview") }

        val interviewEntity = db.save(InterviewEntity(name, null, null, false, chronicler, storyteller))

        val dbQuestions = questionIds
            ?.filter { qid -> questionService.findQuestionEntityByAltId(qid).isPresent }
            ?.map { qid ->
                val q = questionService.findQuestionEntityByAltId(qid)
                val iq = InterviewQuestionEntity()
                iq.interview = interviewEntity
                iq.question = q.getOrElse { throw Exception("Question not found") }
                iq.name = interviewEntity.name + "_" + q.getOrNull()?.name
                interviewQuestionService.save(iq)
            }?.toMutableList()

        interviewEntity.interviewQuestionData = dbQuestions

        return interviewMapper.entityToModel(interviewEntity)
    }

    //TODO: Footing-1 Refactor to a scheduled interview service
    fun scheduleInterview(storytellerId: String, interviewId: String, time: Instant?, name: String?, append: Boolean): String {
        //TODO: Footing-3 Can't schedule before onboarding has completed
        val storyteller = storytellerService.findStorytellerEntityByAltId(storytellerId).getOrElse { throw Exception("Storyteller not found") }
        val interview = findInterviewEntityByAltId(interviewId);

        val scheduledTime: Instant = if(time == null ) {
            if(storyteller.preferredTimes == null || storyteller.preferredTimes?.first()?.time == null || storyteller.preferredTimes?.first()?.dayOfWeek == null) {
                throw Exception("No specified time or preferred time set")
            }
            if(append)
            {
                getNextAvailablePreferredTime(storyteller)
            } else {
                val preferredTime = storyteller.preferredTimes?.first()!!
                val nextDateTime: ZonedDateTime = getNextPreferredTime(preferredTime)
                nextDateTime.toInstant()
            }
        } else {
            time
        }

        val scheduledInterview = ScheduledInterview(null, name ?: "unnamed", emptyList(), scheduledTime, interviewMapper.entityToModel(interview))

        val existingInterview = storyteller.id?.let {
            scheduledInterviewRepository.findByStorytellerIdAndScheduledTime(
                it, Timestamp.from(scheduledTime)
            )
        }
        if(existingInterview != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Duplicate Scheduled Interview ID: " + Base36Encoder.encodeAltId(existingInterview.id.toString()))
        }

        val scheduledInterviewEntity = scheduledInterviewEntityMapper.modelToEntity(scheduledInterview)
        scheduledInterviewEntity.interview?.id = interview.id
        return scheduledInterviewRepository.save(scheduledInterviewEntity).id.toString()
    }

    private fun getNextPreferredTime(preferredTime: PreferredTimeEntity): ZonedDateTime {
        val timeOfDay: LocalTime = LocalTime.from(preferredTime.time!!.toLocalTime())
        val now: ZonedDateTime = ZonedDateTime.now()
        val desiredDayOfWeek: DayOfWeek = DayOfWeek.valueOf(preferredTime.dayOfWeek!!.uppercase())
        val nextDayOfWeek: ZonedDateTime = now.with(TemporalAdjusters.next(desiredDayOfWeek))
        return nextDayOfWeek.with(timeOfDay)
    }

    private fun getNextAvailablePreferredTime(storyteller: StorytellerEntity ): Instant {
        var availablePreferredTimes: List<ZonedDateTime> = emptyList()
        var daysToAdd = 0L
        val nextInterviews = storyteller.id.let {
            scheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(
                it!!
            )
        }
        val scheduledTimes = nextInterviews.map { i -> i.scheduledTime }

        while (availablePreferredTimes.isEmpty()) {
            val now: ZonedDateTime = ZonedDateTime.now()
            val preferredTimes = storyteller.preferredTimes ?: throw Exception("null preferred times")
            val nextPreferredTimes: List<ZonedDateTime> = preferredTimes.map { preferredTime ->
                val nextDateTime: ZonedDateTime = now.plusDays(daysToAdd).with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(preferredTime.dayOfWeek!!.uppercase())))
                    .with(LocalTime.from(preferredTime.time!!.toLocalTime()))
                nextDateTime
            }

            availablePreferredTimes = nextPreferredTimes.filter { nextPreferredTime ->
                val nextPreferredInstant: Instant = nextPreferredTime.toInstant()
                scheduledTimes.none { scheduledTime -> scheduledTime?.toInstant() == nextPreferredInstant } &&
                        nextPreferredTime.isAfter(now)
            }

            daysToAdd++
        }
        val earliestPreferredTime: ZonedDateTime = availablePreferredTimes.min()
        return earliestPreferredTime.toInstant()


    }

    fun getNextInterview(storytellerId: String): ScheduledInterview? {
        val storytellerEntity = storytellerService.findStorytellerEntityByAltId(storytellerId)
        val next = storytellerEntity.getOrElse { throw Exception("Did not find storyteller") }.id?.let {
            scheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(
                it
            )
        }
        if (next != null) {
            if (next.isEmpty()) {
                return null
            }
        }
        if (next != null && next.first().scheduledTime!!.after(Timestamp.from(Instant.now()))) {
            return scheduledInterviewEntityMapper.entityToModel(next.first())
        }

        return null
    }

    fun getAllScheduledInterviews(storytellerId: String): Collection<ScheduledInterview> {
        val storytellerEntity = storytellerService.findStorytellerEntityByAltId(storytellerId)
        storytellerEntity.getOrElse { throw Exception("Did not find storyteller") }.id?.let {
            return scheduledInterviewRepository.findAllByStorytellerIdOrderByScheduledTimeAsc(it)
                .filter { s -> s.scheduledTime != null && s.scheduledTime!!.after(Timestamp.from(Instant.now())) }
                .map { s -> scheduledInterviewEntityMapper.entityToModel(s) }
        }

        return emptyList()
    }

    fun deleteScheduledInterview(scheduledInterviewId: String) {
        return scheduledInterviewRepository.deleteById(scheduledInterviewId.toInt())
    }

    //TODO: What happens if you delete an interview that was scheduled?
    fun deleteInterview(id: String) = db.deleteById(id.toInt())

}