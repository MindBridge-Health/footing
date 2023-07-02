package com.mindbridgehealth.footing.service.model

import java.util.*

data class InterviewQuestion(override var id: String?, override val name: String?, override var tags: List<Tag>? = emptyList(), var interviewId: String?, var questionId: String?, val response : Story?, val completed: Boolean? = false, val skipped: Boolean? = false): Resource()