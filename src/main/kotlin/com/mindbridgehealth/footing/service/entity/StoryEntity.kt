package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "story")
@PrimaryKeyJoinColumn(name="id")
class StoryEntity: ResourceEntity() {

    @Basic
    @Column(name = "text")
    var text: String? = null

    @Basic
    @Column(name = "summary")
    var summary: String? = null

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storyteller: StorytellerEntity? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StoryEntity

        if (text != other.text) return false
        return storyteller == other.storyteller
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (storyteller?.hashCode() ?: 0)
        return result
    }

}
