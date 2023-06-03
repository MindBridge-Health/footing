package com.palaver.service.model

import java.net.URI
import java.util.*

data class Media(override val id: UUID?, override val name: String, override val tags: List<Tag>? = emptyList(), var location: URI?, val type: String, val storyteller: Storyteller?, val story: Story?): Resource