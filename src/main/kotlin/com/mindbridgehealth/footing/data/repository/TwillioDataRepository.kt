package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.TwillioData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TwillioDataRepository: JpaRepository<TwillioData, Int>