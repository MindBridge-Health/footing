package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class StorytellerCreateDtoMapper {

    @Mapping(target = "benefactors", ignore = true)
    @Mapping(target = "preferredChronicler", ignore = true)
    @Mapping(target = "organization", ignore = true)
    abstract fun storytellerCreateDtoToStoryteller(storytellerCreateDto: StorytellerCreateDto): Storyteller

    @AfterMapping
    fun mapOrganization(source: StorytellerCreateDto, @MappingTarget target: Storyteller) {
        target.organization = Organization(source.organizationId?.let { Base36Encoder.decodeAltId(it) }, "", emptyList())
    }
}