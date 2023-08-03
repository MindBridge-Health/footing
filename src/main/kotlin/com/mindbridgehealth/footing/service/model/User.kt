package com.mindbridgehealth.footing.service.model

import java.util.*

abstract class User : DataModel() {
    abstract var lastname: String?
    abstract var firstname: String?
    abstract var middlename: String?
    abstract var email: String?
    abstract var mobile: String?
    abstract var organization: Organization?
    abstract var isActive: Boolean?
    //Avatar
    //Password, Preferences, etc.

}