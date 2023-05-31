package com.palaver.service.mapper

import com.palaver.data.entity.InterviewQuestionEntity
import com.palaver.service.model.InterviewQuestion
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface InterviewQuestionEntityMapper {

    @Mapping(source = "story", target = "response")
    fun entityToModel(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestion

    @Mapping(source = "response", target = "story")
    fun modelToEntity(interviewQuestion: InterviewQuestion): InterviewQuestionEntity
}