package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.MediaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface MediaRepository: JpaRepository<MediaEntity, Int> {

    fun findByStorytellerId(id: Int): Collection<MediaEntity>

    fun findByAltId(altId: String): Optional<MediaEntity>

    fun findAllByLocationAndStorytellerId(location: String, id: Int): Collection<MediaEntity>
}