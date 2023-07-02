package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.OrganizationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository: JpaRepository<OrganizationEntity, Int>