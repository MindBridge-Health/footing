package com.mindbridgehealth.footing.service.model

import java.util.*

data class Benefactor(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    override var mobile: String?,
    override var organization: Organization?,
    override var isActive: Boolean?,
) : User()