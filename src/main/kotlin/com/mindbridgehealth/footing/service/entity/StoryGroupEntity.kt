package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "story_group")
@PrimaryKeyJoinColumn(name="id")
class StoryGroupEntity: ResourceEntity() {

    @ManyToOne
    @JoinColumn(name = "storyteller_id", referencedColumnName = "id")
    var storyteller: StorytellerEntity? = null

    //Todo Story Links
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as StoryGroupEntity
        return if (id != that.id) false else name != that.name
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
