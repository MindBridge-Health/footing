package com.palaver.service.mapper

import com.palaver.data.entity.OrganizationEntity
import com.palaver.service.model.Organization
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface OrganizationEntityMapper {

    fun entityToModel(organizationEntity: OrganizationEntity): Organization

    fun modelToEntity(organization: Organization): OrganizationEntity
}