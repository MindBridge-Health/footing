package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.MediaStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaStatusRepository: JpaRepository<MediaStatusEntity, Int> {

}