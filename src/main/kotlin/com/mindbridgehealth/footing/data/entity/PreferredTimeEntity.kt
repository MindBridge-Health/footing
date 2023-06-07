package com.mindbridgehealth.footing.data.entity

import jakarta.persistence.*
import java.sql.Time

@Entity
@Table(name = "preferred_time")
@PrimaryKeyJoinColumn(name="id")
class PreferredTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null


    @ManyToOne(fetch = FetchType.LAZY)
    var storyteller: StorytellerEntity? = null

    @Basic
    @Column(name = "time")
    var time: Time? = null

    @Basic
    @Column(name = "day")
    var dayOfWeek: String? = null
}