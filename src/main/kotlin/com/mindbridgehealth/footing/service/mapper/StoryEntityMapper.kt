package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.model.Story
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class])
abstract class StoryEntityMapper: IdMapper() {

    @Mapping(source="id", target = "id", ignore = true)
    @Mapping(source = "owner", target = "ownerId", ignore = true)
    abstract fun entityToModel(storyEntity: StoryEntity): Story

    @Mapping(source="id", target = "id", ignore = true)
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    abstract fun modelToEntity(story: Story): StoryEntity
}