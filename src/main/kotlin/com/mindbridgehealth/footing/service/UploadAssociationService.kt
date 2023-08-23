package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI

@Component
class UploadAssociationService (private val mediaService: MediaService, private val storytellerService: StorytellerService, private val applicationProperties: ApplicationProperties) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    @SqsListener("\${application.uploadSqsUrl}")
    fun receiveMessage(message: String) {
        try {
            val event = objectMapper.readTree(message)
            val records = event.get("Records")
            if (records != null && records.isArray) {
                for (record in records) {
                    val objectKey = record["s3"]["object"]["key"].asText()
                    val storytellerId = Base36Encoder.decodeAltId(objectKey.substringBefore("_"))
                    val location = URI.create("${applicationProperties.uploadS3Uri}$objectKey")
                    val type = objectKey.substringAfterLast('.')
                    val storytellerOpt = storytellerService.findStorytellerByAltId(storytellerId)
                    if(storytellerOpt.isEmpty) {
                        logger.error("Unable to find storyteller with ID $storytellerId to associate upload $objectKey")
                    } else {
                        val media = Media(null, objectKey, null, location = location, type = type, storytellerOpt.get(), null, location) //ToDo: Add story ID to S3 object metadata? Generate Thumbnail
                        try {
                            mediaService.associateMediaWithStoryteller(media, storytellerId)
                        } catch(e: Exception) {
                            logger.warn("Caught exception storing media", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error parsing S3 event: ${e.message}", e)
        }
    }
}