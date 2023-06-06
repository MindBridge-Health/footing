package com.mindbridgehealth.footing.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "story")
@PrimaryKeyJoinColumn(name="id")
class StoryEntity: ResourceEntity() {

    @Basic
    @Column(name = "text")
    var text: String? = null

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storyteller: StorytellerEntity? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val storyEntity = other as StoryEntity
        if (id != storyEntity.id) return false
        return name == storyEntity.name
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
