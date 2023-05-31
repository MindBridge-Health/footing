package com.palaver.service.mapper

import com.palaver.data.entity.StoryGroupEntity
import com.palaver.service.model.StoryGroup
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StoryGroupEntityMapper {

    fun entityToModel(storyGroupEntity: StoryGroupEntity): StoryGroup
}