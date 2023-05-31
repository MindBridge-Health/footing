package com.palaver.service.model

import java.util.*

abstract class User {
    abstract var id: UUID?
    abstract var lastname: String
    abstract var firstname: String
    abstract var middlename: String?
    //Avatar
    //Password, Preferences, etc.

}