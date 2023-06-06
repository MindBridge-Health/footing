package com.mindbridgehealth.footing.service.mapper

import com.fasterxml.jackson.databind.JavaType
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.model.OnboardingStatus
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
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