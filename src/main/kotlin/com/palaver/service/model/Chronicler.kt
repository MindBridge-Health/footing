package com.palaver.service.model

import java.util.*

data class Chronicler(override var id : UUID?, override var name: String, val isAi : Boolean) : User()