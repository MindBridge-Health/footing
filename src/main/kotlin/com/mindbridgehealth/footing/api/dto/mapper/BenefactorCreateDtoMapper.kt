package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.BenefactorCreateDto
import com.mindbridgehealth.footing.service.model.Benefactor
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class BenefactorCreateDtoMapper {
    
    abstract fun benefactorCreateDtoToBenefactor(benefactorCreateDto: BenefactorCreateDto): Benefactor
}