package com.palaver.service.model

import java.util.*

data class Benefactor(override var id : UUID?, override var lastname: String, override var firstname: String, override var middlename: String?) : User()