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

    @Mapping(source="interviewQuestionData", target = "interviewQuestions", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "owner", target = "ownerId", ignore = true)
    abstract fun entityToModel(interviewEntity: InterviewEntity): Interview

    @Mapping(source = "interviewQuestions", target ="interviewQuestionData", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    abstract fun modelToEntity(interview: Interview): InterviewEntity

    @AfterMapping
    fun mapInterviewQuestions(interview: Interview, @MappingTarget interviewEntity: InterviewEntity) {
        interviewEntity.interviewQuestionData = interview.interviewQuestions?.map { mapInterviewQuestionToInterviewQuestionEntity(it, interviewEntity) }?.toMutableList()
    }

    private fun mapInterviewQuestionToInterviewQuestionEntity(interviewQuestion: InterviewQuestion, interviewEntity: InterviewEntity): InterviewQuestionEntity {
        val interviewQuestionEntity = InterviewQuestionEntity()
        interviewQuestionEntity.interview = interviewEntity
        return interviewQuestionEntity
    }

    @AfterMapping
    fun mapInterviewQuestionEntitiesToModels(interviewEntity: InterviewEntity, @MappingTarget interview: Interview, interviewQuestionEntityMapper: InterviewQuestionEntityMapper) {
        interview.interviewQuestions = interviewEntity.interviewQuestionData?.map {  it.interview = interviewEntity; interviewQuestionEntityMapper.entityToModel(it)}
    }
}