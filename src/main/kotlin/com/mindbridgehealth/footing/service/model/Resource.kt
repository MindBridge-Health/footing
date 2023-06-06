package com.mindbridgehealth.footing.service.model


abstract class Resource: DataModel() {
    abstract override var id: String?
    abstract val name : String?
    abstract val tags : List<Tag>?
}