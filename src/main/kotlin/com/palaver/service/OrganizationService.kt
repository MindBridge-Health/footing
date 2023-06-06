package com.palaver.service

import com.palaver.data.OrganizationRepository
import com.palaver.service.mapper.OrganizationEntityMapper
import com.palaver.service.model.Organization
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