package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "media")
@PrimaryKeyJoinColumn(name="id")
class MediaEntity: ResourceEntity() {

    @Basic
    @Column(name = "location")
    private var location: String? = null

    @Basic
    @Column(name = "type")
    private var type: String? = null

    @ManyToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var story: StoryEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val mediaEntity = other as MediaEntity
        if (id != mediaEntity.id) return false
        if (name != mediaEntity.name) return false
        if (location != mediaEntity.location) return false
        return type == mediaEntity.type
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (location != null) location.hashCode() else 0
        result = 31 * result + if (type != null) type.hashCode() else 0
        return result
    }
}
