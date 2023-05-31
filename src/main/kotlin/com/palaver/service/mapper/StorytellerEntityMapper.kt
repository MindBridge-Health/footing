package com.palaver.service.mapper

import com.palaver.data.entity.StorytellerEntity
import com.palaver.service.model.Storyteller
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StorytellerEntityMapper {

    fun entityToModel(storytellerEntity: StorytellerEntity) : Storyteller

    fun modelToEntity(storyteller: Storyteller) : StorytellerEntity
}