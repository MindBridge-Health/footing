package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = [OrganizationEntityMapper::class, UserMapper::class])
abstract class ChroniclerEntityMapper: IdMapper() {

    @Mapping(source="ai", target="isAi")
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(chroniclerEntity: ChroniclerEntity) : Chronicler

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(chronicler: Chronicler): ChroniclerEntity

}