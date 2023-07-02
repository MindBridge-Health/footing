package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoRecordedEvent(
    val version: String,
    val data: VideoRecordedEventData
): AddPipeEvent("video_recorded")