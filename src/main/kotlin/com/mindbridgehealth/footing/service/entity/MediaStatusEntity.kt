package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import java.util.*
import javax.persistence.OneToOne

@Entity
@Table(name = "media_status")
class MediaStatusEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    var id: Int? = null

    @Basic
    @Column(name = "state")
    var state: String? = null
}
