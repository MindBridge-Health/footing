package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.data.entity.QuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuestionRepository : JpaRepository<QuestionEntity, Int>

