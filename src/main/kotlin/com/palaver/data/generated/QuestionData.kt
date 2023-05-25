package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "question")
class QuestionData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String = ""

    @Basic
    @Column(name = "text")
    var text: String = ""

    @Basic
    @Column(name = "custom")
    var custom: Boolean = false

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as QuestionData
        if (id != that.id) return false
        if (name != that.name) return false
        return if (text != that.text) false else custom == that.custom
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (text != null) text.hashCode() else 0
        result = 31 * result + custom.hashCode()
        return result
    }
}
