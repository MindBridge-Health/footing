package com.mindbridgehealth.footing.data.entity

import com.mindbridgehealth.footing.service.model.InterviewQuestion
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "interview_question")
@PrimaryKeyJoinColumn(name="id")
class InterviewQuestionEntity: ResourceEntity() {

    @Basic
    @Column(name = "completed")
    var completed: Boolean = false

    @Basic
    @Column(name = "skipped")
    var skipped: Boolean = false

    @ManyToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    lateinit var interview: InterviewEntity

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    lateinit var question: QuestionEntity

    @OneToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var story: StoryEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as InterviewQuestionEntity
        return id == that.id && name == that.name && completed == that.completed && skipped == that.skipped && interview == that.interview && question == that.question && story == that.story
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, completed, skipped, interview, question, story)
    }
}
