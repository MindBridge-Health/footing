package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "interview_question")
class InterviewQuestionData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String = ""

    @Basic
    @Column(name = "completed")
    var completed: Boolean = false

    @Basic
    @Column(name = "skipped")
    var skipped: Boolean = false

    @ManyToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    lateinit var interview: InterviewData

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    lateinit var question: QuestionData

    @OneToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    lateinit var story: StoryData

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as InterviewQuestionData
        return id == that.id && name == that.name && completed == that.completed && skipped == that.skipped && interview == that.interview && question == that.question && story == that.story
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, completed, skipped, interview, question, story)
    }
}
