package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.StoryRepository
import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.mapper.StoryEntityMapper
import com.mindbridgehealth.footing.service.model.Story
import com.mindbridgehealth.footing.service.util.Base36Encoder
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional

@Service
@Transactional
class StoryService(private val db: StoryRepository, private val storyMapper: StoryEntityMapper) {

    fun findStoryById(id: String): Optional<Story> {
        val optionalStory = db.findById(id.toInt())
        if(optionalStory.isPresent) {
            return Optional.of(storyMapper.entityToModel(optionalStory.get()))
        }
        return Optional.empty()
    }

    fun findStoryByAltId(altId: String) : Optional<Story> {
        val optionalStory = db.findByAltId(altId)
        if(optionalStory.isPresent) {
            return Optional.of(storyMapper.entityToModel(optionalStory.get()))
        }
        return Optional.empty()
    }

    fun findStoryEntityByAltId(altId: String) : Optional<StoryEntity> {
        return db.findByAltId(altId)
    }

    fun save(story: Story) : Story {
        return storyMapper.entityToModel(db.save(storyMapper.modelToEntity(story.copy(id = null))))
    }

    fun saveEntity(story: StoryEntity): StoryEntity {
        if(story.originalText == null && story.text == null) {
            story.text = "Not yet transcribed"
        }
        return db.save(story)
    }

    fun update(story: Story): Story {
        return storyMapper.entityToModel(db.save(storyMapper.modelToEntity(story)))
    }
}