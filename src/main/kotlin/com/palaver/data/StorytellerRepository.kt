package com.palaver.data


import com.palaver.data.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StorytellerRepository : JpaRepository<StorytellerEntity, Int> {
    fun findByIdAndIsActive(id: Int, isActive: Boolean): Optional<StorytellerEntity>
}

