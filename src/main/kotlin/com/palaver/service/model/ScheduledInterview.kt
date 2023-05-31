package com.palaver.service.model

import java.time.Instant
import java.util.*

data class ScheduledInterview(override val id: UUID?, override val name : String, override var tags: List<Tag>, val scheduledTime: Instant, val interview: Interview ) : Resource
