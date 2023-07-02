package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.QuestionRepository
import com.mindbridgehealth.footing.service.entity.QuestionEntity
import com.mindbridgehealth.footing.service.mapper.QuestionEntityMapper
import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuestionService(private val db: QuestionRepository, private val questionMapper: QuestionEntityMapper) {

    fun findQuestionById(id: String): Optional<Question> {
        val optionalQuestion = db.findById(Base36Encoder.decode(id).toInt())
        if(optionalQuestion.isPresent){
            return Optional.of(questionMapper.entityToModel(optionalQuestion.get()))
        }
        return Optional.empty()
    }

    fun findQuestionEntityById(id: String): Optional<QuestionEntity> {
        return db.findById(Base36Encoder.decode(id).toInt())
    }
    fun getAllQuestions(): Collection<Question> {
        return db.findAll().map { q -> questionMapper.entityToModel(q) }
    }

    fun save(question: Question) : String {
        val questionEntity = questionMapper.modelToEntity(question)
        questionEntity.id = null
        val questionEntity1 = db.save(questionEntity)
        return questionMapper.entityToModel(questionEntity1).id ?: throw Exception()
    }

    fun update(id: String, question: Question): Question {
        findQuestionById(Base36Encoder.decode(id))
        val questionEntity = questionMapper.modelToEntity(question)
        questionEntity.id = Base36Encoder.decode(id).toInt()
        return questionMapper.entityToModel(db.save(questionEntity))
    }

    fun delete(id: String) = db.deleteById(Base36Encoder.decode(id).toInt())
}