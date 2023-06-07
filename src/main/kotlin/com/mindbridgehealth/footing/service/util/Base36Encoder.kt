package com.mindbridgehealth.footing.service.util

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import kotlin.math.floor
import kotlin.random.Random


object Base36Encoder {

    fun encode(string: String): String {
        if (string.length < 5) {
            string.padStart(4, '0')
        }
        val saltyString = Random.nextInt(10, 99).toString() + string + Random.nextInt(10, 99).toString()
        val bytes: ByteArray = saltyString.toByteArray(StandardCharsets.UTF_8)
        return BigInteger(1, bytes).toString(36).reversed()
    }

    fun decode(string: String): String {
        val bytes = BigInteger(string.reversed(), 36).toByteArray()
        val zeroPrefixLength = zeroPrefixLength(bytes)
        return String(bytes, zeroPrefixLength, bytes.size - zeroPrefixLength).drop(2).dropLast(2)
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