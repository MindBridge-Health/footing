package com.mindbridgehealth.footing.service.model

data class Chronicler(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    override var mobile: String?,
    val isAi: Boolean? = false,
    override var organization: Organization?,
    override var isActive: Boolean?,
) : User()