package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.data.repository.SignatureRepository
import com.mindbridgehealth.footing.service.entity.SignatureEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.SignatureGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class AdminService(
    private val smsNotificationService: SmsNotificationService,
    private val applicationProperties: ApplicationProperties,
    private val signatureRepository: SignatureRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendHomeLinkToUser(storyteller: StorytellerEntity) {
        if (storyteller.mobile == null) {
            logger.error("Unable to send link to user ${storyteller.id}, mobile number was null")
            throw Exception("mobile number is null")
        } else {
            val encodeAltId = storyteller.altId?.let { Base36Encoder.encodeAltId(it) }
            val url = "${applicationProperties.rootUrl}/interview.html?userId=$encodeAltId"
            val sig = SignatureGenerator.generateSignature(applicationProperties.mbhKey, url, "")
            val existingSignatureEntity = signatureRepository.findBySignature(sig)
            existingSignatureEntity.ifPresentOrElse({
                it.issued = Timestamp.from(Instant.now())
                signatureRepository.save(it)
            }, {
                val signatureEntity = SignatureEntity().apply {
                    this.userAltId = storyteller.altId
                    this.url = url
                    this.signature = sig
                }
                signatureRepository.save(signatureEntity)
            })
            val signedUrl = "$url&xsig=$sig"
            smsNotificationService.sendMessage(storyteller.mobile!!, "Here is your link: $signedUrl")
        }
    }
}