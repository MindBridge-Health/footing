package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "question")
@PrimaryKeyJoinColumn(name="id")
class QuestionEntity: ResourceEntity() {
    @Basic
    @Column(name = "text")
    var text: String = ""

    @Basic
    @Column(name = "custom")
    var isCustom: Boolean? = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as QuestionEntity
        if (id != that.id) return false
        if (name != that.name) return false
        return if (text != that.text) false else isCustom == that.isCustom
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + text.hashCode()
        result = 31 * result + isCustom.hashCode()
        return result
    }
}
