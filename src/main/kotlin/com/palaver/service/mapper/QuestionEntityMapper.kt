package com.palaver.service.mapper

import com.palaver.data.entity.QuestionEntity
import com.palaver.service.model.Question
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Component

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface QuestionEntityMapper {

    @Mapping(source="custom", target = "isCustom")
    fun entityToModel(questionEntity: QuestionEntity): Question

    fun modelToEntity(question: Question): QuestionEntity
}