package com.palaver.service.mapper

import com.palaver.data.entity.InterviewEntity
import com.palaver.service.model.Interview
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant



@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class, ChroniclerEntityMapper::class, InterviewQuestionEntityMapper::class])
abstract class InterviewEntityMapper: IdMapper() {


    @Mapping(source="interviewQuestionData", target = "interviewQuestions")
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

    @Mapping(source = "interviewQuestions", target ="interviewQuestionData")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(interview: Interview): InterviewEntity

}