package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.QuestionEntity
import com.mindbridgehealth.footing.service.model.Question
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class QuestionEntityMapper: IdMapper() {

    @Mapping(source="custom", target = "isCustom")
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "owner", target = "ownerId", ignore = true)
    abstract fun entityToModel(questionEntity: QuestionEntity): Question

    @Mapping(source="custom", target = "custom")
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    abstract fun modelToEntity(question: Question): QuestionEntity
}