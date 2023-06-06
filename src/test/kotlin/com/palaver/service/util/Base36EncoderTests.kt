package com.palaver.service.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Base36EncoderTests {

    @Test
    fun encodeSingleDigit_decode_getBackValue() {
        val originalString = "1"

        val encodedString = Base36Encoder.encode(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decode(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }

    @Test
    fun encodeDoubleDigit_decode_getBackValue() {
        val originalString = "1000"

        val encodedString = Base36Encoder.encode(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decode(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }

    @Test
    fun encodeMaxInt_decode_getBackValue() {
        val originalString = Int.MAX_VALUE.toString()

        val encodedString = Base36Encoder.encode(originalString)
        println(encodedString)
        assertNotEquals(originalString, encodedString)

        val decodedString = Base36Encoder.decode(encodedString)
        assertEquals(originalString, decodedString)
        println()
    }
}