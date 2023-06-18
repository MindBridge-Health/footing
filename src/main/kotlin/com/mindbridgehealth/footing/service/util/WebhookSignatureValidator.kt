package com.mindbridgehealth.footing.service.util

import com.mindbridgehealth.footing.configuration.ApplicationProperties
import org.springframework.stereotype.Component
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

@Component
class WebhookSignatureValidator(val applicationProperties: ApplicationProperties) {

    /**
     * Generates a base64-encoded signature for a Pipe webhook request.
     * @param key the webhook's key
     * @param url the webhook url
     * @param jsonData the data in JSON format
     * @return the base64-encoded signature
     */
    fun generateSignature(url: String, jsonData: String): String {
        val dataToSign = url + jsonData
        val mac = Mac.getInstance("HmacSHA1")
        val secretKey = SecretKeySpec(applicationProperties.addPipeKey.toByteArray(), "HmacSHA1")
        mac.init(secretKey)
        val signatureBytes = mac.doFinal(dataToSign.toByteArray())
        return Base64.getEncoder().encodeToString(signatureBytes)
    }
}