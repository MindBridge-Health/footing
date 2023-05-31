package com.palaver.service.model

import java.util.*

data class Storyteller(override var id : UUID?, override var lastname: String, override var firstname: String, override var middlename: String?, var contactMethod: String?, var benefactors: Collection<Benefactor>?, var preferredChronicler: Chronicler?) : User()
//todo: revisit id, do we want to use the raw uuid or convert it? https://toddfredrich.com/ids-in-rest-api.html