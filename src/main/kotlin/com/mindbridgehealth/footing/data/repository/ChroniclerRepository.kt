package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChroniclerRepository: JpaRepository<ChroniclerEntity, Int> {
    fun findByIdAndIsActive(id: Int, isActive: Boolean): Optional<ChroniclerEntity>
    fun findByAltIdAndIsActive(altId: String, isActive: Boolean): Optional<ChroniclerEntity>
    fun findByAltId(altId: String): Optional<ChroniclerEntity>
}