package com.palaver.service.model

import java.util.*

data class AccessPolicy(val id: UUID?, var name: String, var allowedResources: MutableList<Resource>, var deniedResources: MutableList<Resource>, var users: MutableList<User>)