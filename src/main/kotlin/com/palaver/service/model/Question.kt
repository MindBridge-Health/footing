package com.palaver.service.model

import java.util.*

data class Question(override var id: String?, override val name : String, override var tags: List<Tag>? = emptyList(), val text : String, val isCustom : Boolean ) : Resource()
