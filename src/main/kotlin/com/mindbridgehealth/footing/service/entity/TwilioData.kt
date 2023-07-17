package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "twilio_data")
@PrimaryKeyJoinColumn(name="id")
class TwilioData: ResourceEntity() {

    @Column(name = "raw_json", columnDefinition = "json")
    var rawJson: String? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var storyId: StoryEntity? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interview_question_id", referencedColumnName = "id")
    var interviewQuestion: InterviewQuestionEntity? = null

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    var status: TwilioStatus? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TwilioData

        if (rawJson != other.rawJson) return false
        if (storyId != other.storyId) return false
        if (interviewQuestion != other.interviewQuestion) return false
        return status == other.status
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (rawJson?.hashCode() ?: 0)
        result = 31 * result + (storyId?.hashCode() ?: 0)
        result = 31 * result + (interviewQuestion?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        return result
    }


}