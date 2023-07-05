package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.TwilioStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TwilioStatusRepository: JpaRepository<TwilioStatus, Int>