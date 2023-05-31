package com.palaver.service.mapper

import com.palaver.data.entity.BenefactorEntity
import com.palaver.service.model.Benefactor
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface BenefactorEntityMapper {

    fun entityToModel(benefactorEntity: BenefactorEntity) : Benefactor

    fun modelToEntity(benefactor: Benefactor) : BenefactorEntity
}