package com.palaver.data

import com.palaver.data.generated.InterviewData
import com.palaver.data.generated.StorytellerData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InterviewRepository : JpaRepository<InterviewData, UUID> {
    fun findByStorytellerId(storyteller: StorytellerData) : Collection<InterviewData>
}

