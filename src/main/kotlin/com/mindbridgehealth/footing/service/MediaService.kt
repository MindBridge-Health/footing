package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.service.mapper.MediaEntityMapper
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.net.URI
import java.util.Optional
import kotlin.jvm.optionals.getOrElse

@Service
class MediaService(
    private val db: MediaRepository,
    private val mediaMapper: MediaEntityMapper,
    private val storytellerService: StorytellerService,
    private val interviewQuestionService: InterviewQuestionService
) { //, private val storyService: StoryService) {

    fun findMediaById(id: String): Optional<Media> {
        val optionalMedia = db.findById(Base36Encoder.decode(id).toInt())
        if (optionalMedia.isPresent) {
            return Optional.of(mediaMapper.entityToModel(optionalMedia.get()))
        }
        return Optional.empty()
    }

    fun findMediaByStorytellerId(id: String): Collection<Media> {
        val storytellerId = storytellerService.findStorytellerEntityById(Base36Encoder.decodeAltId(id)).getOrElse { throw Exception("Storyteller not found") }.id ?: throw Exception("Storyteller not found")
        return  db.findByStorytellerId(storytellerId).map { mediaMapper.entityToModel(it) }
    }

    fun associateMediaWithStoryteller(media: Media, storytellerId: String): String {
        val storyteller = storytellerService.findStorytellerById(Base36Encoder.decode(storytellerId))
            .getOrElse { throw Exception("Could not find storyteller; unable to associate media") }
        val newMedia = media.copy(storyteller = storyteller)
        return db.save(mediaMapper.modelToEntity(newMedia)).id.toString()
    }

    fun associateMediaWithStorytellerFromInterviewQuestion(media: Media, interviewQuestionId: String) {
        val id = Base36Encoder.decodeAltId(interviewQuestionId).toInt()
        val optionalIq = interviewQuestionService.findEntityById(id)
        val storytellerId =
            optionalIq.getOrElse { throw Exception("InterviewQuestion not found. Could not associate with Storyteller") }
                .interview.storyteller?.altId
                ?: throw Exception("Unable to find storyteller")
        val storyteller = storytellerService.findStorytellerEntityById(storytellerId).getOrElse { throw Exception("Unable to find storyteller") }
        val mediaEntity = mediaMapper.modelToEntity(media)
        mediaEntity.storyteller = storyteller
        db.save(mediaEntity)
    }

    //fun associateMediaWithStory(media: Media, id: UUID) = mediaRepository.save(mediaMapper.modelToEntity(media))

    fun deleteMedia(id: String) {
        db.deleteById(Base36Encoder.decode(id).toInt())
    }
}