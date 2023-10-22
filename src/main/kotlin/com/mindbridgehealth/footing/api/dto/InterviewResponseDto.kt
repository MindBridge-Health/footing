package com.mindbridgehealth.footing.api.dto

import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.model.Tag
import java.time.Instant

data class InterviewResponseDto(val id: String, val name: String, var scheduledTime: String?, var interviewId: String?, var question: Question?, var completed: Boolean, val storyId: String, val tags: Collection<Tag>)
