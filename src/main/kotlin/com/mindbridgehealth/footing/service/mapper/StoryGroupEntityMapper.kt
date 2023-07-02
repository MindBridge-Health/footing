package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.StoryGroupEntity
import com.mindbridgehealth.footing.service.model.StoryGroup
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class])
abstract class StoryGroupEntityMapper {

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun entityToModel(storyGroupEntity: StoryGroupEntity): StoryGroup

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun modelToEntity(storyGroup: StoryGroup): StoryGroupEntity
}