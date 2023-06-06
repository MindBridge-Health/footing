package com.palaver.service.mapper

import com.palaver.data.entity.OrganizationEntity
import com.palaver.service.model.Organization
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class OrganizationEntityMapper: IdMapper() {

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(organizationEntity: OrganizationEntity): Organization

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(organization: Organization): OrganizationEntity

}