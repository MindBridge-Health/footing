package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.OrganizationRepository
import com.mindbridgehealth.footing.service.mapper.OrganizationEntityMapper
import com.mindbridgehealth.footing.service.model.Organization
import org.springframework.stereotype.Service

@Service
class OrganizationService(val organizationRepository: OrganizationRepository, val organizationEntityMapper: OrganizationEntityMapper) {

    fun save(organization: Organization): Organization {
        return organizationEntityMapper.entityToModel(organizationRepository.save(organizationEntityMapper.modelToEntity(organization)))
    }

    fun findById(id: String): Organization {
        return organizationEntityMapper.entityToModel(organizationRepository.findById(id.substring(4).toInt()).orElseThrow())
    }
}