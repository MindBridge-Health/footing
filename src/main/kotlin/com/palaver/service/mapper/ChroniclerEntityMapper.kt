package com.palaver.service.mapper

import com.palaver.data.entity.ChroniclerEntity
import com.palaver.service.model.Chronicler
import com.palaver.service.util.Base36Encoder
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class ChroniclerEntityMapper: IdMapper() {

    @Mapping(source="ai", target="isAi")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(chroniclerEntity: ChroniclerEntity) : Chronicler

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(chronicler: Chronicler): ChroniclerEntity

}