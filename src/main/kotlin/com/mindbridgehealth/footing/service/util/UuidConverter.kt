/*
    Copyright 2013, Strategic Gains, Inc.
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.mindbridgehealth.footing.service.util

import java.nio.ByteBuffer
import java.util.*

/**
 * Utility class to convert between a UUID and a short (22-character) string
 * representation of it (and back). Implements a very efficient URL-safe Base64
 * encoding/decoding algorithm to format/parse the UUID.
 *
 * NOTE: There is NO WAY for this algorithm to detect an invalid short-form
 * UUID if it is 22 characters in length and composed of alpha-numeric
 * characters! So be careful to not use the parse() method to check
 * the short-form UUID string for validity.
 *
 * @author toddf
 * @since Mar 13, 2013
 */
object UuidConverter {
    // Varies from standard Base64 by the last two characters in this string ("-" and "_").
    // The standard characters are "+" and "/" respectively, but are not URL safe.
    private val C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray()
    private val I256 = IntArray(256)

    init {
        for (i in C64.indices) {
            I256[C64[i].code] = i
        }
    }

    private val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    private const val ZERO_SHORT_ID = "0000000000000000000000"

    /**
     * Given a UUID instance, return a short (22-character) string
     * representation of it.
     *
     * @param uuid a UUID instance.
     * @return a short string representation of the UUID.
     * @throws NullPointerException if the UUID instance is null.
     * @throws IllegalArgumentException if the underlying UUID implementation is not 16 bytes.
     */
    fun format(uuid: UUID?): String {
        if (uuid == null) throw NullPointerException("Null UUID")
        if (ZERO_UUID == uuid) return ZERO_SHORT_ID
        val bytes = toByteArray(uuid)
        return encodeBase64(bytes)
    }

    /**
     * Given a UUID representation (either a short or long form) string, return a
     * UUID instance from it.
     *
     *
     * If the uuidString is longer than our short, 22-character form (or 24 with padding),
     * it is assumed to be a full-length 36-character UUID string.
     *
     * @param uuidString a string representation of a UUID.
     * @return a UUID instance
     * @throws IllegalArgumentException if the uuidString is not a valid UUID representation.
     * @throws NullPointerException if the uuidString is null.
     */
    fun parse(uuidString: String?): UUID {
        if (uuidString == null) throw NullPointerException("Null UUID string")
        if (uuidString.length > 24) {
            return UUID.fromString(uuidString)
        }
        require(uuidString.length >= 22) { "Short UUID must be 22 characters: $uuidString" }
        require(!(uuidString.length > 22 && "==" != uuidString.substring(22))) { "Invalid short UUID: $uuidString" }
        if (ZERO_SHORT_ID == uuidString) return ZERO_UUID
        val bytes = decodeBase64(uuidString)
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.put(bytes, 0, 16)
        bb.clear()
        val result = UUID(bb.getLong(), bb.getLong())
        require(ZERO_UUID != result) { "Invalid short UUID: $uuidString" }
        return result
    }

    /**
     * Extracts the bytes from a UUID instance in MSB, LSB order.
     *
     * @param uuid a UUID instance.
     * @return the bytes from the UUID instance.
     */
    private fun toByteArray(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    /**
     * Accepts a UUID byte array (of exactly 16 bytes) and base64 encodes it, using a URL-safe
     * encoding scheme.  The resulting string will be 22 characters in length with no extra
     * padding on the end (e.g. no "==" on the end).
     *
     *
     * Base64 encoding essentially takes each three bytes from the array and converts them into
     * four characters.  This implementation, not using padding, converts the last byte into two
     * characters.
     *
     * @param bytes a UUID byte array.
     * @return a URL-safe base64-encoded string.
     */
    private fun encodeBase64(bytes: ByteArray?): String {
        if (bytes == null) throw NullPointerException("Null UUID byte array")
        require(bytes.size == 16) { "UUID must be 16 bytes" }

        // Output is always 22 characters.
        val chars = CharArray(22)
        var i = 0
        var j = 0
        while (i < 15) {
            // Get the next three bytes.
            val d = bytes[i++].toInt() and 0xff shl 16 or (bytes[i++].toInt() and 0xff shl 8) or (bytes[i++].toInt() and 0xff)

            // Put them in these four characters
            chars[j++] = C64[d ushr 18 and 0x3f]
            chars[j++] = C64[d ushr 12 and 0x3f]
            chars[j++] = C64[d ushr 6 and 0x3f]
            chars[j++] = C64[d and 0x3f]
        }

        // The last byte of the input gets put into two characters at the end of the string.
        val d = bytes[i].toInt() and 0xff shl 10
        chars[j++] = C64[d shr 12]
        chars[j] = C64[d ushr 6 and 0x3f]
        return String(chars)
    }

    /**
     * Base64 decodes a short, 22-character UUID string (or 24-characters with padding)
     * into a byte array. The resulting byte array contains 16 bytes.
     *
     *
     * Base64 decoding essentially takes each four characters from the string and converts
     * them into three bytes. This implementation, not using padding, converts the final
     * two characters into one byte.
     *
     * @param s
     * @return
     */
    private fun decodeBase64(s: String?): ByteArray {
        if (s == null) throw NullPointerException("Cannot decode null string")
        require(!(s.isEmpty() || s.length > 24)) { "Invalid short UUID" }

        // Output is always 16 bytes (UUID).
        val bytes = ByteArray(16)
        var i = 0
        var j = 0
        while (i < 15) {
            // Get the next four characters.
            val d = getChar(s, j++) shl 18 or (getChar(s, j++) shl 12) or (getChar(s, j++) shl 6) or getChar(s, j++)

            // Put them in these three bytes.
            bytes[i++] = (d shr 16).toByte()
            bytes[i++] = (d shr 8).toByte()
            bytes[i++] = d.toByte()
        }

        // Add the last two characters from the string into the last byte.
        bytes[i] = (I256[s[j++].code] shl 18 or (I256[s[j].code] shl 12) shr 16).toByte()
        return bytes
    }

    private fun getChar(s: String, j: Int): Int {
        val c = s[j]
        val v = I256[c.code]
        require(!(v == 0 && c != 'A')) { "Invalid character in short UUID: $c" }
        return v
    }
}