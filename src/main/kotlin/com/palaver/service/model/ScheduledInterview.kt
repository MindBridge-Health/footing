package com.palaver.service.model

import java.util.*

data class ScheduledInterview(override val id: UUID?, override val name : String, val interview: Interview ) : Resource
