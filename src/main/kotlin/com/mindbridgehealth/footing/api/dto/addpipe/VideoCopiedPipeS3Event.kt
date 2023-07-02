package com.mindbridgehealth.footing.api.dto.addpipe

data class VideoCopiedPipeS3Event(
    val version: String,
    val data: VideoCopiedPipeS3EventData
): AddPipeEvent("video_copied_pipe_s3")