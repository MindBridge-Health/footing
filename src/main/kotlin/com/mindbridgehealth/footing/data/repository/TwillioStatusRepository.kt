package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.TwillioStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TwillioStatusRepository: JpaRepository<TwillioStatus, Int>