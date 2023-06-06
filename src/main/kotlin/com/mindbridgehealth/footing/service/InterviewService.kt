package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.entity.ChroniclerEntity
import com.mindbridgehealth.footing.data.entity.InterviewEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder

import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class InterviewService(
    private val db: com.mindbridgehealth.footing.data.InterviewRepository,
    private val storytellerService: StorytellerService,
    private val questionService: QuestionService,
    private val interviewMapper: InterviewEntityMapper,
    private val interviewQuestionService: InterviewQuestionService,
    private val interviewQuestionMapper: InterviewQuestionEntityMapper,
    private val storytellerMapper: StorytellerEntityMapper,
    private val chroniclerMapper: ChroniclerEntityMapper
) {

    fun findInterviewById(interviewId: String): Interview {
        return interviewMapper.entityToModel(db.findById(Base36Encoder.decode(interviewId).toInt()).orElseThrow()) //TODO: Revisit exception
    }

    fun findByStorytellerId(storytellerId: String): Collection<Interview> {
        val storytellerQueryEntity = StorytellerEntity()
        storytellerQueryEntity.id = storytellerId.toInt()
        val interviewResultData = db.findByStorytellerId(storytellerQueryEntity)

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
        val dbChronicler = ChroniclerEntity()
        dbChronicler.id = chroniclerId.toInt()
        val dbQuestions = questions
            .filter { qid -> questionService.findQuestionById(qid).isPresent }
            .map { qid ->
                val q = questionService.findQuestionById(qid)
                val iq = InterviewQuestion(null, q.get().name, null, q.get(), null, completed = false, skipped = false)
                interviewQuestionMapper.modelToEntity(interviewQuestionService.save(iq))
            }.toMutableList()

        val interview = InterviewEntity(name, null, false, null, storytellerMapper.modelToEntity(storyteller))
        interview.interviewQuestionData = dbQuestions
        if (save) {
            return interviewMapper.entityToModel(db.save(interview))
        }

        return Interview(
            null,
            name,
            emptyList(),
            storyteller,
            chroniclerMapper.entityToModel(dbChronicler),
            null,
            false,
            Collections.emptyList()
        )
    }

    fun deleteInterview(id: String) = db.deleteById(Base36Encoder.decode(id).toInt())

}