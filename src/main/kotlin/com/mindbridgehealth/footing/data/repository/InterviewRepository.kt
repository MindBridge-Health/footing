package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.InterviewEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InterviewRepository : JpaRepository<InterviewEntity, Int> {
    fun findByStorytellerId(storytellerId: Int) : Collection<InterviewEntity>
}

