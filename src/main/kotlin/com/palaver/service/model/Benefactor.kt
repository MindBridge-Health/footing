package com.palaver.service.model

import java.util.*

data class Benefactor(override var id : UUID?, override var name: String) : User()