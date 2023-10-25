package com.mindbridgehealth.footing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File
import java.nio.file.Paths

@Component
class UploadAssociationService (private val mediaService: MediaService, private val storytellerService: StorytellerService, private val applicationProperties: ApplicationProperties, private val amazonS3: AmazonS3) {

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
                    val type = objectKey.substringAfterLast('.')

                    if (type.equals("heic", ignoreCase = true)) {
                        logger.info("HEIC detected")
                        val bucketName = record["s3"]["bucket"]["name"].asText()
                        try {
                            convertHeicToJpg(bucketName, objectKey)
                        } catch (e: Exception) {
                            logger.warn("Exception converting HEIC", e)
                        }

                    }

                    val storytellerId = Base36Encoder.decodeAltId(objectKey.substringBefore("_"))
                    val location = URI.create("${applicationProperties.uploadS3Uri}$objectKey")
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
            logger.error("Error handling S3 event: ${e.message}", e)
        }
    }

    private fun convertHeicToJpg(bucketName: String, objectKey: String) {
        val inputPath = "/tmp/$objectKey"
        val convertedImageName = objectKey.replace(".heic", ".jpg").replace(".HEIC", ".jpg")
        val outputPath = "/tmp/$convertedImageName"

        // 1. Download HEIC file from S3 to /tmp
        logger.info("Downloading...")
        downloadObject(bucketName, objectKey, inputPath)

        // 2. Convert HEIC to JPEG using ImageMagick
        logger.info("HEIC Conversion")
        val cmd = ConvertCmd()
        cmd.run(IMOperation().apply {
            addImage(inputPath)
            addImage(outputPath)
        })

        // 3. Upload the converted JPEG back to S3
        logger.info("Uploading")
        uploadObject(bucketName, convertedImageName, outputPath)

        // 4. Optionally, delete the temporary files
        File(inputPath).delete()
        File(outputPath).delete()
    }

    private fun downloadObject(bucketName: String, objectKey: String, filePath: String) {
        val s3Object = amazonS3.getObject(bucketName, objectKey)
        val inputStream = s3Object.objectContent
        File(filePath).outputStream().use { it.write(inputStream.readBytes()) }
    }

    private fun uploadObject(bucketName: String, objectKey: String, filePath: String) {
        val putObjectRequest = PutObjectRequest(bucketName, objectKey, File(filePath))
        logger.info("Putting $objectKey in $bucketName")
        amazonS3.putObject(putObjectRequest)
    }
}