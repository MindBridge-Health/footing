package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.StoryRepository
import com.mindbridgehealth.footing.service.mapper.StoryEntityMapper
import com.mindbridgehealth.footing.service.model.Story
import com.mindbridgehealth.footing.service.util.Base36Encoder
import java.util.Optional

class StoryService(private val db: StoryRepository, private val storyMapper: StoryEntityMapper) {

    fun findStoryById(id: String): Optional<Story> {
        val optionalStory = db.findById(Base36Encoder.decode(id).toInt())
        if(optionalStory.isPresent) {
            return Optional.of(storyMapper.entityToModel(optionalStory.get()))
        }
        return Optional.empty()
    }

    fun save(story: Story) : Story {
        return storyMapper.entityToModel(db.save(storyMapper.modelToEntity(story.copy(id = null))))
    }

    fun update(story: Story): Story {
        return storyMapper.entityToModel(db.save(storyMapper.modelToEntity(story)))
    }
}