package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "twillio_status")
class TwillioStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "call_sid")
    var callSid: String? = null

    @Column(name = "call_status")
    var callStatus: String? = null

    @Column(name = "recording_sid")
    var recordingSid: String? = null

    @Column(name = "recording_status")
    var recordingStatus: String? = null

    @Column(name = "transcription_sid")
    var transcriptionSid: String? = null

    @Column(name = "transcription_status")
    var transcriptionStatus: String? = null

    @OneToMany
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    var data: MutableList<TwillioData>? = null
}
