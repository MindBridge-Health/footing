package com.palaver.service.model

import java.util.*

data class Story(override val id: UUID?, override val name : String, override var tags: List<Tag>? = emptyList(), val text : String, val storyteller: Storyteller ) : Resource
