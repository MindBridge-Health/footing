package com.palaver.data.generated

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "interview")
class InterviewData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String? = null

    @Basic
    @Column(name = "time_completed")
    var timeCompleted: Timestamp? = null

    @Basic
    @Column(name = "completed")
    var completed: Boolean? = null

    @ManyToOne
    @JoinColumn(name = "chronicler_id", referencedColumnName = "id")
    var chroniclerId: ChroniclerData? = null

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storytellerId: StorytellerData? = null

    @OneToMany
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    var interviewQuestionData: MutableList<InterviewQuestionData> = Collections.emptyList()

    protected constructor()
    constructor(name: String?, timeCompleted: Timestamp?, completed: Boolean?, chroniclerId: ChroniclerData?, storytellerId: StorytellerData?) {
        this.name = name
        this.timeCompleted = timeCompleted
        this.completed = completed
        this.chroniclerId = chroniclerId
        this.storytellerId = storytellerId
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as InterviewData
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
