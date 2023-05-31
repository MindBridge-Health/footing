package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tag")
@PrimaryKeyJoinColumn(name="id")
class TagEntity: ResourceEntity() {
    @Basic
    @Column(name = "text")
    var text: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val tagEntity = other as TagEntity
        if (id != tagEntity.id) return false
        return if (name != tagEntity.name) false else text == tagEntity.text
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (text != null) text.hashCode() else 0
        return result
    }
}
