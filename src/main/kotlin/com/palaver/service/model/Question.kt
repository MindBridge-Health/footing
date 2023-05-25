package com.palaver.service.model

import java.util.*

data class Question(override val id: UUID?, override val name : String, val text : String, val isCustom : Boolean ) : Resource
