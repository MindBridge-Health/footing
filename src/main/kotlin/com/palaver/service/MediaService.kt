package com.palaver.service

import com.palaver.data.MediaRepository
import com.palaver.service.mapper.MediaEntityMapper
import com.palaver.service.model.Media
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class MediaService(val mediaRepository: MediaRepository, val mediaMapper: MediaEntityMapper, val storytellerService: StorytellerService) { //, val storyService: StoryService) {

    fun getMedia(id: UUID) = mediaMapper.entityToModel(mediaRepository.findById(id).getOrElse { throw Exception() }) //todo revisit exception
    fun associateMediaWithStoryteller(media: Media, storytellerId: UUID): UUID {
        val storyteller = storytellerService.findStorytellerById(storytellerId)
            .getOrElse { throw Exception("Could not find storyteller; unable to associate media") }
        val newMedia = media.copy(storyteller=storyteller)
        return mediaRepository.save(mediaMapper.modelToEntity(newMedia)).id ?: throw Exception("Unable to save Media")
    }
    //fun associateMediaWithStory(media: Media, id: UUID) = mediaRepository.save(mediaMapper.modelToEntity(media))

    fun deleteMedia(id: UUID) {
        mediaRepository.deleteById(id)
    }
}