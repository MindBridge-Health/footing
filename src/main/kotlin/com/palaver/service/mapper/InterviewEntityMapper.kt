package com.palaver.service.mapper

import com.palaver.data.entity.InterviewEntity
import com.palaver.service.model.Interview
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import java.sql.Timestamp
import java.time.Instant




@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class InterviewEntityMapper {

    @Mapping(source = "timeCompleted", target = "timeCompleted", ignore = true)
    fun entityToModel(interviewEntity: InterviewEntity): Interview {

        val chronicler = if (interviewEntity.chronicler == null) null
        else Mappers.getMapper(ChroniclerEntityMapper::class.java).entityToModel(
            interviewEntity.chronicler!!
        )

        val storyteller = if (interviewEntity.storyteller == null) null
        else Mappers.getMapper(StorytellerEntityMapper::class.java).entityToModel(
            interviewEntity.storyteller!!
        )

        val interviewQuestions = interviewEntity.interviewQuestionData
            ?.map { i -> Mappers.getMapper(InterviewQuestionEntityMapper::class.java).entityToModel(i) }
            ?.toList()
        
        return Interview(
            interviewEntity.id,
            interviewEntity.name ?: "",
            emptyList(),
            storyteller,
            chronicler,
            interviewEntity.timeCompleted?.toInstant(),
            interviewEntity.completed ?: false,
            interviewQuestions ?: emptyList()
        )
    }

    @Mapping(source = "timeCompleted", target = "timeCompleted", ignore = true)
    abstract fun modelToEntity(interview: Interview): InterviewEntity

}