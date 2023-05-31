package com.palaver.service.model

import java.util.*

data class StoryGroup(val id: UUID?, var name: String, var storyteller: Storyteller, var stories: List<Story>)