package com.palaver.service.model

import java.util.*

data class Chronicler(override var id : UUID?, override var lastname: String, override var firstname: String, override var middlename: String?, val isAi : Boolean) : User()