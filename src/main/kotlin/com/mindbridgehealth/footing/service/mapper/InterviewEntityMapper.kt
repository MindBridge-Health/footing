package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.InterviewEntity
import com.mindbridgehealth.footing.service.entity.InterviewQuestionEntity
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import org.mapstruct.*
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class, ChroniclerEntityMapper::class, InterviewQuestionEntityMapper::class])
abstract class InterviewEntityMapper: IdMapper() {

    //abstract var interviewQuestionEntityMapper: InterviewQuestionEntityMapper

    @Mapping(source="interviewQuestionData", target = "interviewQuestions", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(interviewEntity: InterviewEntity): Interview
//    {
//
//        val chronicler = if (interviewEntity.chronicler == null) null
//        else Mappers.getMapper(ChroniclerEntityMapper::class.java).entityToModel(
//            interviewEntity.chronicler!!
//        )
//
//        val storyteller = if (interviewEntity.storyteller == null) null
//        else Mappers.getMapper(StorytellerEntityMapper::class.java).entityToModel(
//            interviewEntity.storyteller!!
//        )
//
//        val interviewQuestions = interviewEntity.interviewQuestionData
//            ?.map { i -> Mappers.getMapper(InterviewQuestionEntityMapper::class.java).entityToModel(i) }
//            ?.toList()
//
//        return Interview(
//            interviewEntity.id,
//            interviewEntity.name ?: "",
//            emptyList(),
//            storyteller,
//            chronicler,
//            interviewEntity.timeCompleted?.toInstant(),
//            interviewEntity.completed ?: false,
//            interviewQuestions ?: emptyList()
//        )
//    }

    @Mapping(source = "interviewQuestions", target ="interviewQuestionData", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(interview: Interview): InterviewEntity

    @AfterMapping
    fun mapInterviewQuestions(interview: Interview, @MappingTarget interviewEntity: InterviewEntity) {
        interviewEntity.interviewQuestionData = interview.interviewQuestions?.map { mapInterviewQuestionToInterviewQuestionEntity(it) }?.toMutableList()
    }

    fun mapInterviewQuestionToInterviewQuestionEntity(interviewQuestion: InterviewQuestion): InterviewQuestionEntity {
        val interviewQuestionEntity = InterviewQuestionEntity()
        interviewQuestionEntity.interview = modelToEntity(interviewQuestion.interview!!)
        return interviewQuestionEntity
    }

//    @AfterMapping
//    fun mapInterviewQuestionEntities(interviewEntity: InterviewEntity, @MappingTarget interview: Interview) {
//        val interviewQuestions = interview.interviewQuestions
//        if(interviewQuestions == null) {
//            interview.interviewQuestions = interviewEntity.interviewQuestionData?.map { mapInterviewQuestionEntitiesToInterviewQuestions(it) }?.toList() ?: emptyList()
//        }
//    }
//
//    fun mapInterviewQuestionEntitiesToInterviewQuestions(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestion {
//        val interviewQuestion = interviewQuestionEntityMapper!!.entityToModel(interviewQuestionEntity)
//        return interviewQuestion
//    }
}