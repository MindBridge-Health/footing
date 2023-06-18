package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoConvertedEvent(
    val version: String,
    val data: VideoConvertedEventData
): AddPipeEvent("video_converted")