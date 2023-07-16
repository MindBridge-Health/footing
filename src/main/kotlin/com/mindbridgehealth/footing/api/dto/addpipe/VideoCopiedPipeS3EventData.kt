package com.mindbridgehealth.footing.api.dto.addpipe

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

data class VideoCopiedPipeS3EventData(
    val storedStatus: String,
    val videoName: String,
    val size: String,
    @JsonProperty("checksum_md5")
    val checksumMd5: String,
    @JsonProperty("checksum_sha1")
    val checksumSha1: String,
    val id: String,
    val url: URL,
    val rawRecordingUrl: URL?,
    val snapshotUrl: URL?,
    val filmstripUrl: URL?,
    val cdn: CdnData?,
    val bucket: String?,
    val region: String?,
    val payload: String?
)