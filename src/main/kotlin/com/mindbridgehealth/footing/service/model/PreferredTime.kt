package com.mindbridgehealth.footing.service.model

import java.sql.Time
import java.time.DayOfWeek

data class PreferredTime(
    var storyteller: Storyteller?,
    var time: Time,
    var dayOfWeek: DayOfWeek
)
