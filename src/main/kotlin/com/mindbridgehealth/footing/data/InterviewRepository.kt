package com.mindbridgehealth.footing.data

import com.mindbridgehealth.footing.data.entity.InterviewEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InterviewRepository : JpaRepository<InterviewEntity, Int> {
    fun findByStorytellerId(storyteller: StorytellerEntity) : Collection<InterviewEntity>
}

