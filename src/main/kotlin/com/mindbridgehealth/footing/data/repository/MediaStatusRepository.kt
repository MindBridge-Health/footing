package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.MediaEntity
import com.mindbridgehealth.footing.service.entity.MediaStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface MediaStatusRepository: JpaRepository<MediaStatusEntity, Int> {

}