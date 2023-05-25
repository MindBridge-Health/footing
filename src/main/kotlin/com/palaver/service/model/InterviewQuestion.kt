package com.palaver.service.model

import java.util.*

data class InterviewQuestion(override val id: UUID?, override val name : String, val question : Question, val response : String, val completed: Boolean, val skipped : Boolean): Resource