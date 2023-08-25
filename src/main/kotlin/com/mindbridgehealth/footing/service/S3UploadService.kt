package com.mindbridgehealth.footing.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.GetObjectRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Date

@Service
class S3UploadService(private val amazonS3: AmazonS3) { // Inject AmazonS3 instance

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${aws.bucket.name}")
    private val bucketName: String = "" // Set your S3 bucket name in application.properties

    fun generatePresignedUrlForObject(key: String, duration: Duration, storytellerId: String): String {
        return generatePresignedUrlForObject(key, duration, storytellerId, HttpMethod.PUT)
    }

    fun generatePresignedUrlForObject(key: String, duration: Duration, storytellerId: String, httpMethod: HttpMethod): String {
        val expiration = Date(System.currentTimeMillis() + duration.toMillis())

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, key)
            .withMethod(httpMethod)
            .withExpiration(expiration)

        val preSignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest)

        logger.info("Presigned Url: $preSignedUrl")

        return preSignedUrl.toString()
    }

    fun getObject(objectKey: String): ByteArray {
            val getObjectRequest = GetObjectRequest(bucketName, objectKey)

            val s3Object = amazonS3.getObject(getObjectRequest)
            val objectStream = s3Object.objectContent

            return objectStream.readAllBytes()
    }
}
