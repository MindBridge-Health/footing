package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoConvertedEventData(
    val videoName: String,
    val duration: Int,
    val audioCodec: String,
    val videoCodec: String,
    val type: String,
    val size: Int,
    val width: String,
    val height: String,
    val orientation: String,
    val id: Int,
    val dateTime: String,
    val timeZone: String,
    val payload: String
)
