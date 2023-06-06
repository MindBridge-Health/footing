package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.InterviewQuestionRepository
import com.mindbridgehealth.footing.service.mapper.InterviewQuestionEntityMapper
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InterviewQuestionService(private val interviewQuestionRepository: InterviewQuestionRepository, private val interviewQuestionEntityMapper: InterviewQuestionEntityMapper) {

    fun findById(id: String) {
        interviewQuestionRepository.findById(id.toInt())
    }
    fun save(interviewQuestion: InterviewQuestion) : InterviewQuestion {
        val interviewQuestionEntity = interviewQuestionEntityMapper.modelToEntity(interviewQuestion)
        interviewQuestionEntity.id = null
        return interviewQuestionEntityMapper.entityToModel(interviewQuestionRepository.save(interviewQuestionEntity))
    }
}