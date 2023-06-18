package com.mindbridgehealth.footing.api.dto.addpipe

import java.net.URL

data class CdnData(
    val cdnRecordingUrl: URL,
    val cdnRawRecordingUrl: URL,
    val cdnSnapshotUrl: URL,
    val cdnFilmstripUrl: URL
)