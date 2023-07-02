package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.StoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoryRepository : JpaRepository<StoryEntity, Int>

