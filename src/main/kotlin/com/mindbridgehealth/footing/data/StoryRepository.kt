package com.mindbridgehealth.footing.data

import com.mindbridgehealth.footing.data.entity.StoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoryRepository : JpaRepository<StoryEntity, Int>

