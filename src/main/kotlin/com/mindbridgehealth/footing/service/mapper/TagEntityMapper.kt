package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.TagEntity
import com.mindbridgehealth.footing.service.model.Tag
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class TagEntityMapper: IdMapper() {

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun entityToModel(tagEntity: TagEntity): Tag

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun modelToEntity(tag: Tag): TagEntity
}