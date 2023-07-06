package com.mindbridgehealth.footing.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val audience: String,
    val addPipeKey: String,
    val twilioKey: String) {
}