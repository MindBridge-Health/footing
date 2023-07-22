package com.mindbridgehealth.footing.api.dto

import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.model.User
import java.util.*

data class ChroniclerCreateDto(
    var lastname: String?,
    var firstname: String?,
    var middlename: String?,
    var mobile: String?,
    var contactMethod: String?,
    val isAi: Boolean? = false,
    var organizationId: String?
)
