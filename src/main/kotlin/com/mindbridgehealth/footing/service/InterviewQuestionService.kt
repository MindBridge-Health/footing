package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.service.entity.InterviewQuestionEntity
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
        val interviewQuestionEntity = interviewQuestionRepository.findById(id).get()
        return Optional.of(interviewQuestionEntityMapper.entityToModel(interviewQuestionEntity))
    }

    fun findByAltId(altId: String): Optional<InterviewQuestion> {
        val interviewQuestionEntity = interviewQuestionRepository.findById(altId.toInt())
        if(interviewQuestionEntity.isEmpty) {
            return Optional.empty()
        }
        return Optional.of(interviewQuestionEntityMapper.entityToModel(interviewQuestionEntity.get()))
    }

    fun findEntityById(id: Int): Optional<InterviewQuestionEntity> {
        return interviewQuestionRepository.findById(id)
    }

    fun findEntityByAltId(altId: String): Optional<InterviewQuestionEntity> {
        return interviewQuestionRepository.findByAltId(altId)
    }


    fun save(interviewQuestion: InterviewQuestion) : InterviewQuestionEntity {
        val interviewQuestionEntity = interviewQuestionEntityMapper.modelToEntity(interviewQuestion)
        interviewQuestionEntity.id = null
        return interviewQuestionRepository.save(interviewQuestionEntity)
    }

    fun save(interviewQuestionEntity: InterviewQuestionEntity): InterviewQuestionEntity {
        return interviewQuestionRepository.save(interviewQuestionEntity)
    }
}