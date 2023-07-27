package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.SignatureEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.sql.Timestamp
import java.util.*

interface SignatureRepository: JpaRepository<SignatureEntity, Int> {

    fun findBySignature(signature: String): Optional<SignatureEntity>

    fun findAllByIssuedBefore(timestamp: Timestamp): List<SignatureEntity>
}