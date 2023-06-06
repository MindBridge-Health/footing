package com.mindbridgehealth.footing.api.dto

import com.mindbridgehealth.footing.service.model.User
import java.util.*

data class StorytellerCreateDto(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    var contactMethod: String?,
    var benefactors: Collection<String>?,
    var preferredChronicler: UUID?
) : User()
