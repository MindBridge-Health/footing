package com.palaver.service

import com.palaver.data.QuestionRepository
import com.palaver.service.mapper.QuestionEntityMapper
import com.palaver.service.model.Question
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuestionService(val db: QuestionRepository, @Autowired val questionMapper: QuestionEntityMapper) {

    fun findQuestionById(id: UUID) = questionMapper.entityToModel(db.findById(id).orElseThrow()) //TODO cleanup exception case

    fun save(question: Question) : Question {
        val questionEntity = questionMapper.modelToEntity(question)
        questionEntity.id = null
        return questionMapper.entityToModel(db.save(questionEntity))
    }

    fun update(id: UUID, question: Question): Question {
        findQuestionById(id)
        val questionEntity = questionMapper.modelToEntity(question)
        questionEntity.id = id
        return questionMapper.entityToModel(db.save(questionEntity))
    }

}