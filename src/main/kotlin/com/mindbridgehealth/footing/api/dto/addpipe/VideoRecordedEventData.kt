package com.mindbridgehealth.footing.api.dto.addpipe

import com.fasterxml.jackson.annotation.JsonProperty

data class VideoRecordedEventData(
    val videoName: String,
    val audioCodec: String,
    val videoCodec: String,
    val type: String,
    val id: String,
    val dateTime: String,
    val timeZone: String,
    val payload: String,
    val httpReferer: String,
    @JsonProperty("cam_name")
    val camName: String,
    @JsonProperty("mic_name")
    val micName: String,
    val ip: String,
    val ua: String
)
