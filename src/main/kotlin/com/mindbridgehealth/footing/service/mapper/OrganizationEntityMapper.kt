package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.OrganizationEntity
import com.mindbridgehealth.footing.service.model.Organization
import org.mapstruct.*
import org.springframework.stereotype.Service

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class OrganizationEntityMapper: IdMapper() {

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(organizationEntity: OrganizationEntity): Organization

    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(organization: Organization): OrganizationEntity

}