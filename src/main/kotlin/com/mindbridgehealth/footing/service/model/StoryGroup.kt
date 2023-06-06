package com.mindbridgehealth.footing.service.model

import java.util.*

data class StoryGroup(val id: String?, var name: String, var storyteller: Storyteller, var stories: List<Story>)