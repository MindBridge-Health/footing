package com.mindbridgehealth.footing.service.util

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignatureGenerator {

    /**
     * Generates a base64-encoded signature for a Pipe webhook request.
     * @param key the key with which to sign
     * @param url the webhook url
     * @param jsonData the data in JSON format
     * @return the base64-encoded signature
     */
    fun generateSignature(key: String, url: String, jsonData: String): String {
        val dataToSign = url + jsonData
        val mac = Mac.getInstance("HmacSHA1")
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
        mac.init(secretKey)
        val signatureBytes = mac.doFinal(dataToSign.toByteArray())
        return Base64.getEncoder().encodeToString(signatureBytes)
    }

    fun validateSignature(key: String, url: String, jsonData: String, signature: String): Boolean {
        println(generateSignature(key, url, jsonData))
        return signature == generateSignature(key, url, jsonData)
    }
}