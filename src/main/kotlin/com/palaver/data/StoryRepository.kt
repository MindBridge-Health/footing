package com.palaver.data

import com.palaver.data.generated.StoryData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoryRepository : JpaRepository<StoryData, UUID>

