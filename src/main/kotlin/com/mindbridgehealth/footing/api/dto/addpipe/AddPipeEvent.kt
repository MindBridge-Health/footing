package com.mindbridgehealth.footing.api.dto.addpipe

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "event"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = VideoRecordedEvent::class, name = "video_recorded"),
    JsonSubTypes.Type(value = VideoConvertedEvent::class, name = "video_converted"),
    JsonSubTypes.Type(value = VideoCopiedPipeS3Event::class, name = "video_copied_pipe_s3")
)
open class AddPipeEvent(val event: String)