package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.ChroniclerCreateDto
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class ChroniclerCreateDtoMapper {

    @Mapping(target = "organization", ignore = true)
    abstract fun chroniclerCreateDtoToChronicler(chroniclerCreateDto: ChroniclerCreateDto): Chronicler

    @AfterMapping
    fun mapOrganization(source: ChroniclerCreateDto, @MappingTarget target: Chronicler) {
        target.organization = Organization(source.organizationId?.let { Base36Encoder.decodeAltId(it) }, "", emptyList())
    }
}