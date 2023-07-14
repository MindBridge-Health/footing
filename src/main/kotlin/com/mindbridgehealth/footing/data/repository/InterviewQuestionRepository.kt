package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.InterviewQuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InterviewQuestionRepository : JpaRepository<InterviewQuestionEntity, Int> {

    fun findByAltId(altId: String): Optional<InterviewQuestionEntity>

    fun findByInterviewId(id: Int): Collection<InterviewQuestionEntity>
}

