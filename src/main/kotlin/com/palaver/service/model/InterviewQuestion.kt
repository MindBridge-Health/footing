package com.palaver.service.model

import java.util.*

data class InterviewQuestion(override val id: UUID?, override val name: String?, override var tags: List<Tag>? = emptyList(), val question: Question?, val response : Story?, val completed: Boolean? = false, val skipped: Boolean? = false): Resource