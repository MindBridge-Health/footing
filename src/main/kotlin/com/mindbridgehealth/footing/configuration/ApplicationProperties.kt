package com.mindbridgehealth.footing.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val audience: String,
    val addPipeKey: String,
    val twilioSid: String,
    val twilioKey: String,
    val rootUrl: String,
    val mbhKey: String)