package com.palaver.data.entity

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "scheduled_interview")
class ScheduledInterviewEntity {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "scheduled_time")
    var scheduledTime: Timestamp? = null

    @OneToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    var interview: InterviewEntity? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ScheduledInterviewEntity
        if (id != that.id) return false
        return scheduledTime == that.scheduledTime
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (scheduledTime != null) scheduledTime.hashCode() else 0
        return result
    }
}
