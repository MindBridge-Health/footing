package com.mindbridgehealth.footing.service.entity

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
    var interview: InterviewEntity? = null

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    var question: QuestionEntity? = null

    @OneToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var story: StoryEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as InterviewQuestionEntity

        if (completed != other.completed) return false
        if (skipped != other.skipped) return false
        if (interview != other.interview) return false
        if (question != other.question) return false
        return story == other.story
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + skipped.hashCode()
        result = 31 * result + (interview?.hashCode() ?: 0)
        result = 31 * result + (question?.hashCode() ?: 0)
        result = 31 * result + (story?.hashCode() ?: 0)
        return result
    }


}
