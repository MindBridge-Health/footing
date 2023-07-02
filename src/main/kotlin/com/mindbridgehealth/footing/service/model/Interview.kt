package com.mindbridgehealth.footing.service.model

import java.time.Instant
import java.util.*

data class Interview(override var id: String?, override val name : String, override var tags: List<Tag>? = emptyList(), val storyteller: Storyteller?, val chronicler: Chronicler?, val timeCompleted : Instant?, val completed : Boolean, var interviewQuestions: Collection<InterviewQuestion>?) : Resource()
