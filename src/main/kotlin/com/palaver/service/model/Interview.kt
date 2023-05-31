package com.palaver.service.model

import java.time.Instant
import java.util.*

data class Interview(override val id: UUID?, override val name : String, override var tags: List<Tag>? = emptyList(), val storyteller: Storyteller?, val chronicler: Chronicler?, val timeCompleted : Instant?, val completed : Boolean, val interviewQuestions: Collection<InterviewQuestion>) : Resource
