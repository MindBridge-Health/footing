package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "twilio_data")
class TwilioData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "status_id")
    var statusId: Int? = null

    @Column(name = "raw_json", columnDefinition = "json")
    var rawJson: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwilioData

        if (id != other.id) return false
        if (statusId != other.statusId) return false
        return rawJson == other.rawJson
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (statusId ?: 0)
        result = 31 * result + (rawJson?.hashCode() ?: 0)
        return result
    }


}