package com.palaver.service.mapper

import com.palaver.data.entity.BenefactorEntity
import com.palaver.service.model.Benefactor
import com.palaver.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class BenefactorEntityMapper: IdMapper() {

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(benefactorEntity: BenefactorEntity) : Benefactor

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(benefactor: Benefactor) : BenefactorEntity

}