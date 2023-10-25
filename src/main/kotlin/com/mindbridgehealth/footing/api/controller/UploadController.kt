package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.S3UploadService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.time.Duration

@RestController
@RequestMapping("/api/v1/uploads/images")
class UploadController(private val s3UploadService: S3UploadService, private val restTemplate: RestTemplate) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{imageName}/upload-url")
    fun getPresignedUrl(@AuthenticationPrincipal principal: Jwt, @PathVariable imageName: String): ResponseEntity<String> {
        val preSignedUrl = s3UploadService.generatePresignedUrlForObject(imageName, Duration.ofMinutes(15), principal.subject)
        return ResponseEntity.ok(preSignedUrl)
    }

    //TODO: Can a benefactor upload on behalf of?
    @GetMapping("/storytellers/{storytellerId}/{imageName}/upload-url")
    fun getPresignedUrlOnBehalfOf(@AuthenticationPrincipal principal: Jwt, @PathVariable storytellerId: String, @PathVariable imageName: String): ResponseEntity<String> {
        val preSignedUrl = s3UploadService.generatePresignedUrlForObject(imageName, Duration.ofMinutes(15), Base36Encoder.decodeAltId(storytellerId))
        return ResponseEntity.ok(preSignedUrl)
    }

    @GetMapping("/storytellers/{imageName}/upload-url")
    fun getPresignedUrlForSelf(@AuthenticationPrincipal principal: Jwt, @PathVariable imageName: String): ResponseEntity<String> {
        val storytellerId = principal.subject
        val preSignedUrl = s3UploadService.generatePresignedUrlForObject(imageName, Duration.ofMinutes(15), storytellerId)
        return ResponseEntity.ok(preSignedUrl)
    }

    @GetMapping("/proxy/{imageName}")
    fun proxyImage(@PathVariable("imageName") imageName: String): ResponseEntity<ByteArray> {

        return try {
            val imageResponse = s3UploadService.getObject(imageName)
            val headers = HttpHeaders()
            if (imageName.endsWith(".jpg", true) || imageName.endsWith(".jpeg", true)) {
                headers.contentType = MediaType.IMAGE_JPEG
            } else if (imageName.endsWith(".png", true)) {
                headers.contentType = MediaType.IMAGE_PNG
            } else if (imageName.endsWith(".heic", true)) {
                headers.contentType = MediaType.valueOf("image/heic")
            }
            ResponseEntity(imageResponse, headers, HttpStatus.OK)
        } catch (e: IOException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
