package com.palaver.data


import com.palaver.data.generated.StorytellerData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StorytellerRepository : JpaRepository<StorytellerData, UUID>

