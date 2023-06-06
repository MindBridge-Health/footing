package com.mindbridgehealth.footing.service.model

import java.util.*

data class AccessPolicy(val id: String?, var name: String, var allowedResources: MutableList<Resource>, var deniedResources: MutableList<Resource>, var users: MutableList<User>)