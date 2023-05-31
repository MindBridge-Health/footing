package com.palaver.service.model

import java.util.*

data class Media(override val id: UUID?, override var name: String, override var tags: List<Tag>? = emptyList(), var location: String, var type: String): Resource