package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.data.entity.MbUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MindBridgeUserRepository: JpaRepository<MbUserEntity, Int> {

    fun findByAltIdAndIsActive(altId: String, isActive: Boolean): Optional<MbUserEntity>
}