package com.mindbridgehealth.footing.service.model

import java.util.UUID

data class Organization(override var id : String?, override val name : String, override val tags: List<Tag>?): Resource()
