package com.mindbridgehealth.footing.service.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Base36EncoderTests {

    @Test
    fun encodeSingleDigit_decode_getBackValue() {
        val originalString = "1"

        val encodedString = Base36Encoder.encodeAltId(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decodeAltId(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }

    @Test
    fun encodeDoubleDigit_decode_getBackValue() {
        val originalString = "1000"

        val encodedString = Base36Encoder.encodeAltId(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decodeAltId(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }

    @Test
    fun encodeMaxInt_decode_getBackValue() {
        val originalString = Int.MAX_VALUE.toString()

        val encodedString = Base36Encoder.encodeAltId(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decodeAltId(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }

    @Test
    fun encodeIdProvider_decode_getBackValue() {
        val originalString = "google-oauth2"

        val encodedString = Base36Encoder.encodeAltId(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decodeAltId(encodedString)
        assertEquals(originalString, decodedString)
        println(decodedString)
    }

    @Test
    fun encodeAltId_decode_getBackValue() {
        val altId = "auth0|648a23ab6ee6f0aa87941142"

        val encoded = Base36Encoder.encodeAltId(altId)
        println(encoded)

        val decoded = Base36Encoder.decodeAltId(encoded)
        println(decoded)

        assertEquals(altId, decoded)

    }

}