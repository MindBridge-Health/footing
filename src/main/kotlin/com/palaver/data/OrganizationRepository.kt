package com.palaver.data

import com.palaver.data.entity.OrganizationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository: JpaRepository<OrganizationEntity, Int>