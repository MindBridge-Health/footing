package com.mindbridgehealth.footing.data.repository


import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StorytellerRepository : JpaRepository<StorytellerEntity, Int> {
    fun findByIdAndIsActive(id: Int, isActive: Boolean): Optional<StorytellerEntity>
    fun findByAltIdAndIsActive(altId: String, isActive: Boolean): Optional<StorytellerEntity>
    fun findByAltId(altId: String): Optional<StorytellerEntity>
}

