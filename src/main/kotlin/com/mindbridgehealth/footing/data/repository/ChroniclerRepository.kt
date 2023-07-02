package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChroniclerRepository: JpaRepository<ChroniclerEntity, Int>