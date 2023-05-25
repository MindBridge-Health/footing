package com.palaver.data

import com.palaver.data.generated.InterviewQuestionData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InterviewQuestionRepository : JpaRepository<InterviewQuestionData, UUID>

