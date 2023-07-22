package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.model.OnboardingStatus
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = [BenefactorEntityMapper::class, ChroniclerEntityMapper::class, PreferredTimeMapper::class, OrganizationEntityMapper::class]
)
abstract class StorytellerEntityMapper : IdMapper() {

    @Mapping(target = "onboardingStatus", ignore = true) //todo Onboarding status
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(storytellerEntity: StorytellerEntity): Storyteller

    @Mapping(target = "onboardingStatus", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(storyteller: Storyteller): StorytellerEntity

    @AfterMapping
    fun storytellerAfterMapping(source: Storyteller, @MappingTarget target: StorytellerEntity) {
        target.onboardingStatus = source.onboardingStatus?.value
    }

    @AfterMapping
    fun storytellerAfterMapping(source: StorytellerEntity, @MappingTarget target: Storyteller) {
        target.onboardingStatus = OnboardingStatus.getByValue(source.onboardingStatus ?: 0)
    }

}