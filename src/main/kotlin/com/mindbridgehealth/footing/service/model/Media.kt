package com.mindbridgehealth.footing.service.model

import java.net.URI
import java.util.*

data class Media(override var id: String?, override val name: String, override val tags: List<Tag>? = emptyList(), var location: URI?, val type: String, val storyteller: Storyteller?, val story: Story?, var thumbnail: URI?): Resource()