package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.StoryEntity
import com.mindbridgehealth.footing.service.model.Story
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class])
abstract class StoryEntityMapper {

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun entityToModel(storyEntity: StoryEntity): Story

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun modelToEntity(story: Story): StoryEntity
}