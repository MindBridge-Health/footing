package com.palaver.service.mapper

import com.palaver.data.entity.ChroniclerEntity
import com.palaver.service.model.Chronicler
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ChroniclerEntityMapper {

    @Mapping(source="ai", target="isAi")
    fun entityToModel(chroniclerEntity: ChroniclerEntity) : Chronicler

    fun modelToEntity(chronicler: Chronicler): ChroniclerEntity
}