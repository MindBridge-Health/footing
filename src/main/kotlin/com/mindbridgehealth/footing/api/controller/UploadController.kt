package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.S3UploadService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api/v1/uploads/images")
class UploadController(private val s3UploadService: S3UploadService) {

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
        logger.info("Presigned Url (Controller): $preSignedUrl")
        return ResponseEntity.ok(preSignedUrl)
    }
}
