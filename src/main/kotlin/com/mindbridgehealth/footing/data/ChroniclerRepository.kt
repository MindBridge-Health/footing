package com.mindbridgehealth.footing.data

import com.mindbridgehealth.footing.data.entity.ChroniclerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChroniclerRepository: JpaRepository<ChroniclerEntity, Int>