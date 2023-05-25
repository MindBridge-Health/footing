package com.palaver.data.generated

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "scheduled_interview")
class ScheduledInterviewData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "scheduled_time")
    var scheduledTime: Timestamp? = null

    @OneToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    var interview: InterviewData? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ScheduledInterviewData
        if (id != that.id) return false
        return scheduledTime == that.scheduledTime
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (scheduledTime != null) scheduledTime.hashCode() else 0
        return result
    }
}
