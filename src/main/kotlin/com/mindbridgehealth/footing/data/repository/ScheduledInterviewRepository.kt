package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.data.entity.InterviewEntity
import com.mindbridgehealth.footing.data.entity.ScheduledInterviewEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.UUID

@Repository
interface ScheduledInterviewRepository : JpaRepository<ScheduledInterviewEntity, Int> {
    @Query("SELECT a FROM ScheduledInterviewEntity a JOIN a.interview b where b.storyteller.id = :storytellerId ORDER BY a.scheduledTime ASC")
    fun findAllByStorytellerIdOrderByScheduledTimeAsc(@Param("storytellerId") storytellerId: Int) : Collection<ScheduledInterviewEntity>

    @Query("SELECT a FROM ScheduledInterviewEntity a JOIN a.interview b where b.storyteller.id = :storytellerId AND a.scheduledTime = :scheduledTime")
    fun findByStorytellerIdAndScheduledTime(@Param("storytellerId") storytellerId: Int, @Param("scheduledTime") scheduledTime: Timestamp): ScheduledInterviewEntity?
}

