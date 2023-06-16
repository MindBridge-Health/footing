package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.data.entity.BenefactorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BenefactorRepository: JpaRepository<BenefactorEntity, Int> {

    fun findByIdAndIsActive(id: Int, isActive: Boolean): Optional<BenefactorEntity>
    fun findByAltIdAndIsActive(altId: String, isActive: Boolean): Optional<BenefactorEntity>
    fun findByAltId(altId: String): Optional<BenefactorEntity>
}