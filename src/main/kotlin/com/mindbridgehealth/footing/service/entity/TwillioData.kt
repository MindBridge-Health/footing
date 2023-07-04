package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "twillio_data")
class TwillioData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "status_id")
    var statusId: Int? = null

    @Column(name = "raw_json", columnDefinition = "json")
    var rawJson: String? = null
}