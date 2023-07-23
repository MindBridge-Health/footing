package com.mindbridgehealth.footing.service.model

data class Story(
    override var id: String?,
    override val name: String,
    override var tags: List<Tag>? = emptyList(),
    val text: String,
    val summary: String?,
    val storyteller: Storyteller
) : Resource()
