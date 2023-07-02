package com.mindbridgehealth.footing.service.entity

import com.mindbridgehealth.footing.service.model.Interview
import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

@Entity
@Table(name = "interview")
@PrimaryKeyJoinColumn(name="id")
class InterviewEntity: ResourceEntity {
    @Basic
    @Column(name = "time_completed")
    var timeCompleted: Timestamp? = null

    @Basic
    @Column(name = "completed")
    var completed: Boolean? = null

    @ManyToOne
    @JoinColumn(name = "chronicler_id", referencedColumnName = "id")
    var chronicler: ChroniclerEntity? = null

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storyteller: StorytellerEntity? = null

    @OneToMany
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    var interviewQuestionData: MutableList<InterviewQuestionEntity>? = null

    constructor() : super()
    constructor(name: String?, timeCompleted: Timestamp?, completed: Boolean?, chronicler: ChroniclerEntity?, storyteller: StorytellerEntity?): super(name) {
        this.timeCompleted = timeCompleted
        this.completed = completed
        this.chronicler = chronicler
        this.storyteller = storyteller
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as InterviewEntity
        if (id != that.id) return false
        if (name != that.name) return false
        return if (timeCompleted != that.timeCompleted) false else completed == that.completed
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (timeCompleted != null) timeCompleted.hashCode() else 0
        result = 31 * result + if (completed != null) completed.hashCode() else 0
        return result
    }
}
