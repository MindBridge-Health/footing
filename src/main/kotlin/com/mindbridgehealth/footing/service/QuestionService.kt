package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.QuestionRepository
import com.mindbridgehealth.footing.service.entity.QuestionEntity
import com.mindbridgehealth.footing.service.mapper.QuestionEntityMapper
import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class QuestionService(private val db: QuestionRepository, private val questionMapper: QuestionEntityMapper) {

    fun findQuestionById(id: Int): Optional<Question> {
        val optionalQuestion = db.findById(id)
        if(optionalQuestion.isPresent){
            return Optional.of(questionMapper.entityToModel(optionalQuestion.get()))
        }
        return Optional.empty()
    }

    fun findQuestionByAltId(altId: String): Optional<Question> {
        val optionalQuestion = db.findByAltId(altId)
        if(optionalQuestion.isPresent){
            return Optional.of(questionMapper.entityToModel(optionalQuestion.get()))
        }
        return Optional.empty()
    }

    fun findQuestionEntityByAltId(altId: String): Optional<QuestionEntity> {
        return db.findByAltId(altId)
    }
    fun getAllQuestions(): Collection<Question> {
        return db.findAll()
            .filter { q -> q.owner == null }
            .map { q -> questionMapper.entityToModel(q) }
    }

    fun save(question: Question) : String {
        val questionEntity = questionMapper.modelToEntity(question)
        questionEntity.id = null
        val questionEntity1 = db.save(questionEntity)
        return questionMapper.entityToModel(questionEntity1).id ?: throw Exception()
    }

    fun update(id: String, question: Question): Question {
        val storedEntity = findQuestionEntityByAltId(id).getOrElse { throw Exception("Could not find Question to update") }
        val questionEntity = questionMapper.modelToEntity(question)
        storedEntity.name = questionEntity.name
        storedEntity.text = questionEntity.text
        storedEntity.tags = questionEntity.tags
        storedEntity.isCustom = questionEntity.isCustom
        return questionMapper.entityToModel(db.save(questionEntity))
    }

    fun delete(id: String) = db.deleteById(id.toInt())
}