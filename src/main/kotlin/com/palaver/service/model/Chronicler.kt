package com.palaver.service.model

import java.util.*

data class Chronicler(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    val isAi: Boolean? = false
) : User()