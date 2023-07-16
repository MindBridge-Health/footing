package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoConvertedEventData(
    val videoName: String,
    val duration: String,
    val audioCodec: String,
    val videoCodec: String,
    val type: String,
    val size: String,
    val width: String,
    val height: String,
    val orientation: String,
    val id: String,
    val dateTime: String,
    val timeZone: String,
    val payload: String
)
