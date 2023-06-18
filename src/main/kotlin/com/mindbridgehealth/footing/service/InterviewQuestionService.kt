package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.InterviewQuestionRepository
import com.mindbridgehealth.footing.service.mapper.InterviewQuestionEntityMapper
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.model.InterviewQuestion
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class InterviewQuestionService(private val interviewQuestionRepository: InterviewQuestionRepository, private val interviewQuestionEntityMapper: InterviewQuestionEntityMapper) {

    fun findById(id: Int): Optional<InterviewQuestion> {
        return Optional.of(interviewQuestionEntityMapper.entityToModel(interviewQuestionRepository.findById(id).get()))
    }

    fun findByAltId(altId: String): Optional<InterviewQuestion> {
        val id = Base36Encoder.decode(altId).toInt()
        val interviewQuestionEntity = interviewQuestionRepository.findById(id)
        if(interviewQuestionEntity.isEmpty) {
            return Optional.empty()
        }
        return Optional.of(interviewQuestionEntityMapper.entityToModel(interviewQuestionEntity.get()))
    }
    fun save(interviewQuestion: InterviewQuestion) : InterviewQuestion {
        val interviewQuestionEntity = interviewQuestionEntityMapper.modelToEntity(interviewQuestion)
        interviewQuestionEntity.id = null
        return interviewQuestionEntityMapper.entityToModel(interviewQuestionRepository.save(interviewQuestionEntity))
    }
}