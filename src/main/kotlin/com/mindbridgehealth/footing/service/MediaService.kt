package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.service.mapper.MediaEntityMapper
import com.mindbridgehealth.footing.service.model.Media
import org.springframework.stereotype.Service
import java.util.Optional
import kotlin.jvm.optionals.getOrElse

@Service
class MediaService(
    private val db: MediaRepository,
    private val mediaMapper: MediaEntityMapper,
    private val storytellerService: StorytellerService,
    private val interviewQuestionService: InterviewQuestionService
) {

    fun findMediaById(id: String): Optional<Media> {
        val optionalMedia = db.findById(id.toInt())
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
        val storyteller = storytellerService.findStorytellerByAltId(storytellerId)
            .getOrElse { throw Exception("Could not find storyteller; unable to associate media") }
        val newMedia = media.copy(storyteller = storyteller)
        return db.save(mediaMapper.modelToEntity(newMedia)).id.toString()
    }

    fun associateMediaWithStorytellerFromInterviewQuestion(media: Media, interviewQuestionId: String) {
        val altId = interviewQuestionId
        val optionalIq = interviewQuestionService.findEntityByAltId(altId)
        val storytellerId =
            optionalIq.getOrElse { throw Exception("InterviewQuestion not found. Could not associate with Storyteller") }
                .interview?.storyteller?.id
                ?: throw Exception("Interview Question did not have interview or storyteller associated")
        val storyteller = storytellerService.findStorytellerEntityById(storytellerId).getOrElse { throw Exception("Unable to find storyteller") }
        val mediaEntity = mediaMapper.modelToEntity(media)
        mediaEntity.storyteller = storyteller
        db.save(mediaEntity)
    }

    /**
     * This function is only meant to update the status that comes from add pipe.
     * It is not intended to be a general update function.
     */
    fun updateMediaStatus(media: Media, interviewQuestionId: String) {
        val incomingMedia =  mediaMapper.modelToEntity(media)
        val mediaEntityOptional =  incomingMedia.altId?.let {db.findByAltId(it) } ?: Optional.empty()
        if (mediaEntityOptional.isPresent) { //TODO: Check that storyteller matches or is null e.g. isn't assigned yet
            val mediaEntity = mediaEntityOptional.get()
            mediaEntity.type = incomingMedia.type
            mediaEntity.location = incomingMedia.location
            mediaEntity.state = incomingMedia.state
            db.save(mediaEntity)
        } else {
            associateMediaWithStorytellerFromInterviewQuestion(media, interviewQuestionId)
        }
    }

    fun deleteMedia(id: String) {
        db.deleteById(id.toInt())
    }
}