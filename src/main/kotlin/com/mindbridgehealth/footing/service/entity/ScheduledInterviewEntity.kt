package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "scheduled_interview")
class ScheduledInterviewEntity: ResourceEntity() {

    @Basic
    @Column(name = "scheduled_time")
    var scheduledTime: Timestamp? = null

    @OneToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    var interview: InterviewEntity? = null

    @Basic
    @Column(name = "link_sent")
    var linkSent: Boolean? = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ScheduledInterviewEntity

        if (scheduledTime != other.scheduledTime) return false
        if (interview != other.interview) return false
        return linkSent == other.linkSent
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (scheduledTime?.hashCode() ?: 0)
        result = 31 * result + (interview?.hashCode() ?: 0)
        result = 31 * result + (linkSent?.hashCode() ?: 0)
        return result
    }


}
