package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.InterviewEntity
import com.mindbridgehealth.footing.data.entity.InterviewQuestionEntity
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[StorytellerEntityMapper::class, QuestionEntityMapper::class, TimeMapper::class])
abstract class InterviewQuestionEntityMapper(): IdMapper() {

    @Mapping(target="interview", ignore = true)
    @Mapping(source = "story", target = "response")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestion

    @Mapping(target="interview", ignore = true)
    @Mapping(source = "response", target = "story")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(interviewQuestion: InterviewQuestion): InterviewQuestionEntity

    @AfterMapping
    fun mapInterviewQuestionEntities(interviewEntity: InterviewEntity, @MappingTarget interview: Interview) {
        val interviewQuestions = interview.interviewQuestions
        if(interviewQuestions == null) {
            interview.interviewQuestions = interviewEntity.interviewQuestionData?.map { mapInterviewQuestionEntitiesToInterviewQuestions(it) }?.toList() ?: emptyList()
        }
    }

    fun mapInterviewQuestionEntitiesToInterviewQuestions(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestion {
        val interviewQuestion = entityToModel(interviewQuestionEntity)
        return interviewQuestion
    }

}