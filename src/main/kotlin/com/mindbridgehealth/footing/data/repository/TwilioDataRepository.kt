package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.TwilioData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TwilioDataRepository: JpaRepository<TwilioData, Int>