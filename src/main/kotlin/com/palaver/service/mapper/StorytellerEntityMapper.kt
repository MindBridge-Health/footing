package com.palaver.service.mapper

import com.fasterxml.jackson.databind.JavaType
import com.palaver.data.entity.StorytellerEntity
import com.palaver.service.model.OnboardingStatus
import com.palaver.service.model.Storyteller
import com.palaver.service.util.Base36Encoder
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[BenefactorEntityMapper::class, ChroniclerEntityMapper::class])
abstract class StorytellerEntityMapper: IdMapper() {

    @Mapping(target = "onboardingStatus", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(storytellerEntity: StorytellerEntity) : Storyteller

    @Mapping(target = "onboardingStatus", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(storyteller: Storyteller) : StorytellerEntity

}