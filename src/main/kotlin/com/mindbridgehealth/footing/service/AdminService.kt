package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.SignatureGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AdminService(private val smsNotificationService: SmsNotificationService, private val applicationProperties: ApplicationProperties) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendHomeLinkToUser(storyteller: Storyteller) {
        if(storyteller.mobile == null) {
            logger.error("Unable to send link to user ${storyteller.id}, mobile number was null")
            throw Exception("mobile number is null")
        } else {
            val url = "${applicationProperties.rootUrl}/userhome.html"
            val sig = SignatureGenerator.generateSignature(applicationProperties.mbhKey, url, "")
            val signedUrl = "$url?xsig=$sig"
            smsNotificationService.sendMessage(storyteller.mobile!!, "Here is your link: $signedUrl")
        }
    }
}