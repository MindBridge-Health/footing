package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoRecordedEventData(
    val videoName: String,
    val audioCodec: String,
    val videoCodec: String,
    val type: String,
    val id: Int,
    val dateTime: String,
    val timeZone: String,
    val payload: String,
    val httpReferer: String,
    val camName: String,
    val micName: String,
    val ip: String,
    val ua: String
)
