package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.time.Instant

@Entity
@Table(name = "signature")
class SignatureEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    var id: Int? = null

    @Basic
    @Column(name = "user_alt_id")
    var userAltId: String? = null

    @Basic
    @Column(name = "url")
    var url: String? = null

    @Basic
    @Column(name = "issued")
    var issued: Timestamp? = Timestamp.from(Instant.now())

    @Basic
    @Column(name = "signature")
    var signature: String? = null

}