package com.mindbridgehealth.footing.service.util

import java.math.BigInteger
import java.nio.charset.StandardCharsets


object Base36Encoder {

    enum class idProvi
    fun encode(string: String): String {
        if (string.length < 5) {
            string.padStart(4, '0')
        }
        val bytes = string.toByteArray(StandardCharsets.UTF_8)
        return BigInteger(1, bytes).toString(36).reversed()
    }

    fun decode(string: String): String {
        val bytes = BigInteger(string.reversed(), 36).toByteArray()
        val zeroPrefixLength = zeroPrefixLength(bytes)
        return String(bytes, zeroPrefixLength, bytes.size - zeroPrefixLength)
    }

    fun encodeAltId(altId: String): String {
        val idTokens = altId.split('|')
        val idProvider = encode(idTokens[0])
        val externalId = if(idTokens.size > 1) idProvider + '.' + idTokens[1] else idProvider
        return externalId
    }

    fun decodeAltId(externalId: String): String {
        val idTokens = externalId.split('.')
        val idProvider = decode(idTokens[0])
        val altId = if(idTokens.size > 1) idProvider + '|' + idTokens[1] else idProvider
        return altId
    }

    private fun zeroPrefixLength(bytes: ByteArray): Int {
        for (i in bytes.indices) {
            if (bytes[i].toInt() != 0) {
                return i
            }
        }
        return bytes.size
    }
}