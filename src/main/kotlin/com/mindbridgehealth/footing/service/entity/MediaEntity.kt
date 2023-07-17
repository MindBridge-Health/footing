package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import javax.persistence.OneToOne

@Entity
@Table(name = "media")
@PrimaryKeyJoinColumn(name="id")
class MediaEntity: ResourceEntity() {

    @Basic
    @Column(name = "location")
    var location: String? = null

    @Basic
    @Column(name = "type")
    var type: String? = null

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storyteller: StorytellerEntity? = null

    @ManyToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var story: StoryEntity? = null

    @ManyToOne
    @JoinColumn(name = "media_status_id", referencedColumnName = "id")
    var status: MediaStatusEntity? = null

    @Basic
    @Column(name = "raw_json", columnDefinition = "json")
    var rawJson: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MediaEntity

        if (location != other.location) return false
        if (type != other.type) return false
        if (storyteller != other.storyteller) return false
        if (story != other.story) return false
        return rawJson == other.rawJson
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (storyteller?.hashCode() ?: 0)
        result = 31 * result + (story?.hashCode() ?: 0)
        result = 31 * result + (rawJson?.hashCode() ?: 0)
        return result
    }


}
