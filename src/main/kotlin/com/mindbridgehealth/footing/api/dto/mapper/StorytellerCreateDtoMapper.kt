package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.service.model.Storyteller
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class StorytellerCreateDtoMapper {

    @Mapping(target = "benefactors", ignore = true)
    @Mapping(target = "preferredChronicler", ignore = true)
    abstract fun storytellerCreateDtoToStoryteller(storytellerCreateDto: StorytellerCreateDto): Storyteller
}