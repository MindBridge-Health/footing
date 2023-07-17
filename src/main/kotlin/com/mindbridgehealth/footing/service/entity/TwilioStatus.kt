package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import javax.persistence.OneToOne

@Entity
@Table(name = "twilio_status")
class TwilioStatus {
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwilioStatus

        if (id != other.id) return false
        if (callSid != other.callSid) return false
        if (callStatus != other.callStatus) return false
        if (recordingSid != other.recordingSid) return false
        if (recordingStatus != other.recordingStatus) return false
        if (transcriptionSid != other.transcriptionSid) return false
        return transcriptionStatus == other.transcriptionStatus
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (callSid?.hashCode() ?: 0)
        result = 31 * result + (callStatus?.hashCode() ?: 0)
        result = 31 * result + (recordingSid?.hashCode() ?: 0)
        result = 31 * result + (recordingStatus?.hashCode() ?: 0)
        result = 31 * result + (transcriptionSid?.hashCode() ?: 0)
        result = 31 * result + (transcriptionStatus?.hashCode() ?: 0)
        return result
    }


}
