package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.InterviewQuestionEntity
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[StorytellerEntityMapper::class, QuestionEntityMapper::class])
abstract class InterviewQuestionEntityMapper: IdMapper() {

    @Mapping(source = "story", target = "response")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestion

    @Mapping(source = "response", target = "story")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(interviewQuestion: InterviewQuestion): InterviewQuestionEntity
}