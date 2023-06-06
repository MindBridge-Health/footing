package com.mindbridgehealth.footing.data

import com.mindbridgehealth.footing.data.entity.OrganizationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository: JpaRepository<OrganizationEntity, Int>