package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.service.mapper.MediaEntityMapper
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
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
        val storytellerId = storytellerService.findStorytellerEntityByAltId(id).getOrElse { throw Exception("Storyteller not found") }.id ?: throw Exception("Storyteller not found")
        return  db.findByStorytellerId(storytellerId).map { mediaMapper.entityToModel(it) }
    }

    fun associateMediaWithStoryteller(media: Media, storytellerId: String): String {
        val storyteller = storytellerService.findStorytellerById(storytellerId)
            .getOrElse { throw Exception("Could not find storyteller; unable to associate media") }
        val newMedia = media.copy(storyteller = storyteller)
        return db.save(mediaMapper.modelToEntity(newMedia)).id.toString()
    }

    fun associateMediaWithStorytellerFromInterviewQuestion(media: Media, interviewQuestionId: String) {
        val altId = Base36Encoder.decodeAltId(interviewQuestionId)
        val optionalIq = interviewQuestionService.findEntityByAltId(altId)
        val storytellerId =
            optionalIq.getOrElse { throw Exception("InterviewQuestion not found. Could not associate with Storyteller") }
                .interview.storyteller?.id
                ?: throw Exception("Unable to find storyteller")
        val storyteller = storytellerService.findStorytellerEntityById(storytellerId).getOrElse { throw Exception("Unable to find storyteller") }
        val mediaEntity = mediaMapper.modelToEntity(media)
        mediaEntity.storyteller = storyteller
        db.save(mediaEntity)
    }

    fun updateMediaStatus(media: Media, interviewQuestionId: String) {
        val mediaEntityOptional = db.findByAltId(Base36Encoder.decodeAltId(media.id!!)) //TODO: Error Checking
        if (mediaEntityOptional.isPresent) {
            val mediaEntity = mediaEntityOptional.get()
            val incomingMedia =  mediaMapper.modelToEntity(media)
            mediaEntity.type = incomingMedia.type
            mediaEntity.location = incomingMedia.location
            mediaEntity.state = incomingMedia.state
            db.save(mediaEntity)
        } else {
            associateMediaWithStorytellerFromInterviewQuestion(media, interviewQuestionId)
        }
    }

    //fun associateMediaWithStory(media: Media, id: UUID) = mediaRepository.save(mediaMapper.modelToEntity(media))

    fun deleteMedia(id: String) {
        db.deleteById(Base36Encoder.decode(id).toInt())
    }
}