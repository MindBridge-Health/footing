package com.palaver.service.mapper

import com.palaver.data.entity.StoryEntity
import com.palaver.service.model.Story
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StoryEntityMapper {

    fun entityToModel(storyEntity: StoryEntity): Story

    fun modelToEntity(story: Story): StoryEntity
}