package com.mindbridgehealth.footing.service.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Time
import java.time.DayOfWeek

data class PreferredTime(
    @JsonIgnore
    var storyteller: Storyteller?,
    var time: Time,
    var dayOfWeek: DayOfWeek
)
