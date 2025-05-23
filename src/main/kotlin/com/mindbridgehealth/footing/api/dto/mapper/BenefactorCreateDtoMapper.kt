package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.BenefactorCreateDto
import com.mindbridgehealth.footing.service.model.Benefactor
import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class BenefactorCreateDtoMapper {

    @Mapping(target = "organization", ignore = true)
    abstract fun benefactorCreateDtoToBenefactor(benefactorCreateDto: BenefactorCreateDto): Benefactor

    @AfterMapping
    fun mapOrganization(source: BenefactorCreateDto, @MappingTarget target: Benefactor) {
        target.organization = Organization(source.organizationId?.let { Base36Encoder.decodeAltId(it) }, "", emptyList())
    }
}