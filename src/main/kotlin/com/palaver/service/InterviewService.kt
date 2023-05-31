package com.palaver.service

import com.palaver.data.entity.ChroniclerEntity
import com.palaver.data.entity.InterviewEntity
import com.palaver.data.entity.StorytellerEntity
import com.palaver.service.mapper.*
import com.palaver.service.model.*
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class InterviewService(
    val interviewDb: com.palaver.data.InterviewRepository,
    val storytellerService: StorytellerService,
    val questionService: QuestionService,
    val interviewMapper: InterviewEntityMapper,
    val storytellerMapper: StorytellerEntityMapper,
    val chroniclerMapper: ChroniclerEntityMapper
) {

    fun getById(interviewId: UUID): Interview {
        return interviewMapper.entityToModel(interviewDb.findById(interviewId).orElseThrow()) //TODO: Revisit exception
    }

    fun findByStorytellerId(storytellerId: UUID): Collection<Interview> {
        val storytellerQueryEntity = StorytellerEntity()
        storytellerQueryEntity.id = storytellerId
        val interviewResultData = interviewDb.findByStorytellerId(storytellerQueryEntity)

        return interviewResultData.stream()
            .map { i -> interviewMapper.entityToModel(i) }
            .toList()
    }


    fun createInterview(name: String, chroniclerId: UUID, storytellerId: UUID, questions: List<UUID>, save: Boolean): Interview {
        val storyteller = storytellerService.findStorytellerById(storytellerId).getOrElse { throw Exception("Storyteller was not found; unable to create interview") }
        val dbChronicler = ChroniclerEntity()
        dbChronicler.id = chroniclerId
        val dbQuestions = questions.map { qid -> run {
            val q = questionService.findQuestionById(qid)
            val iq = InterviewQuestion(null, q.name, null, q, null, completed = false, skipped = false)
            Mappers.getMapper(InterviewQuestionEntityMapper::class.java).modelToEntity(iq)
        }}.toMutableList()
        val interview = InterviewEntity(name, null, false, null, storytellerMapper.modelToEntity(storyteller))
        //interview.interviewQuestionData = dbQuestions //TODO Save these first
        if (save) {
            return interviewMapper.entityToModel(interviewDb.save(interview))
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

}