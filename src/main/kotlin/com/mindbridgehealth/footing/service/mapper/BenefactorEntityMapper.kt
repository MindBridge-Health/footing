package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.BenefactorEntity
import com.mindbridgehealth.footing.service.model.Benefactor
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = [OrganizationEntityMapper::class, UserMapper::class])
abstract class BenefactorEntityMapper: IdMapper() {

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(benefactorEntity: BenefactorEntity) : Benefactor

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(benefactor: Benefactor) : BenefactorEntity

}