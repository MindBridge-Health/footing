package com.mindbridgehealth.footing.service.model

import java.util.*

abstract class User : DataModel() {
    abstract var lastname: String?
    abstract var firstname: String?
    abstract var middlename: String?
    abstract var email: String?
    //Avatar
    //Password, Preferences, etc.

}