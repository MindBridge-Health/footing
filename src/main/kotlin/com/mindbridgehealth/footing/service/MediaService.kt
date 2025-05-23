package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.api.dto.addpipe.AddPipeEvent
import com.mindbridgehealth.footing.api.dto.addpipe.VideoCopiedPipeS3Event
import com.mindbridgehealth.footing.data.repository.MediaRepository
import com.mindbridgehealth.footing.data.repository.MediaStatusRepository
import com.mindbridgehealth.footing.service.entity.MediaStatusEntity
import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.mapper.MediaEntityMapper
import com.mindbridgehealth.footing.service.model.Media
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional
class MediaService(
    private val db: MediaRepository,
    private val mediaStatusDb: MediaStatusRepository,
    private val mediaMapper: MediaEntityMapper,
    private val storytellerService: StorytellerService,
    private val interviewQuestionService: InterviewQuestionService,
    val storyService: StoryService,
    private val smsNotificationService: SmsNotificationService
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
        return  db.findByStorytellerIdAndTypeNotIgnoreCase(storytellerId, "heic").map { mediaMapper.entityToModel(it) }
    }

    fun findMediaByStorytellerIdAndName(id: String, name: String): Optional<Media> {
        val storytellerId = storytellerService.findStorytellerEntityByAltId(id).getOrElse { throw Exception("Storyteller not found") }.id ?: throw Exception("Storyteller not found")
        val optionalMediaEntity = db.findByStorytellerIdAndName(storytellerId, name)
        if (optionalMediaEntity.isPresent) {
            return Optional.of(mediaMapper.entityToModel(optionalMediaEntity.get()))
        }
        return Optional.empty()
    }

    fun findMediaByStory(id: String): Collection<Media> {
        val mediaEntities = db.findByStoryAltIdAndTypeNotIgnoreCase(id, "heic")
        return mediaEntities.map{ mediaMapper.entityToModel(it) }
    }

    fun associateMediaWithStoryteller(media: Media, storytellerId: String, storyId: String?): String {
        val storyteller = storytellerService.findStorytellerEntityByAltId(storytellerId)
            .getOrElse { throw Exception("Could not find storyteller; unable to associate media") }
        val mediaEntity = mediaMapper.modelToEntity(media)
        mediaEntity.storyteller = storyteller

        if(storyId != null) {
            mediaEntity.story = storyService.findStoryEntityByAltId(storyId).orElse(null)
        }

        val mediaEntries = db.findAllByLocationAndStorytellerId(mediaEntity.location!!, storyteller.id!!)
        if(!mediaEntries.isEmpty()) {
            throw Exception("Duplicate Entry")
        }
        return db.save(mediaEntity).id.toString()
    }

    fun associateMediaWithStorytellerFromInterviewQuestion(media: Media, interviewQuestionId: String, mediaStatus: MediaStatusEntity) {
        val altId = interviewQuestionId
        val interviewQuestionEntity = interviewQuestionService.findEntityByAltId(altId).getOrElse { throw Exception("InterviewQuestion not found. Could not associate with Storyteller") }

        val mediaEntity = mediaMapper.modelToEntity(media)



        val storyteller =
            interviewQuestionEntity
                .interview?.storyteller
                ?: throw Exception("Interview Question did not have interview or storyteller associated")

        var storyEntity = interviewQuestionEntity.story
        if(storyEntity == null) {
            storyEntity = storyService.saveEntity(StoryEntity().apply {
                this.storyteller = storyteller
                this.name = "AddPipe_InterviewQuestion_$interviewQuestionId"
            })
        }

        mediaEntity.story = storyEntity
        mediaEntity.storyteller = storyteller
        mediaEntity.status = mediaStatus
        db.save(mediaEntity)
        storyteller.mobile?.let { smsNotificationService.sendMessage(it, "Hi ${storyteller.firstname}, this is MindBridge Health. We've received the video file you uploaded for your recent interview. Thank you, and have a wonderful day!") }
    }

    /**
     * This function is only meant to update the status that comes from add pipe.
     * It is not intended to be a general update function.
     */
    fun updateMediaStatus(media: Media, interviewQuestionId: String, addPipeEvent: AddPipeEvent) {

        val mediaStatus = MediaStatusEntity()
        mediaStatus.state = addPipeEvent.event
        val savedMediaStatus = mediaStatusDb.save(mediaStatus)

        if (addPipeEvent is VideoCopiedPipeS3Event) {
            associateMediaWithStorytellerFromInterviewQuestion(media, interviewQuestionId, savedMediaStatus)
        }
    }

    fun deleteMedia(id: String) {
        db.deleteById(id.toInt())
    }
}