package com.palaver.data

import com.palaver.data.entity.InterviewEntity
import com.palaver.data.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InterviewRepository : JpaRepository<InterviewEntity, Int> {
    fun findByStorytellerId(storyteller: StorytellerEntity) : Collection<InterviewEntity>
}

