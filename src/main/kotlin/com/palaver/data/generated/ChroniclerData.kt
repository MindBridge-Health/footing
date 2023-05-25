package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "chronicler")
class ChroniclerData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String? = null

    @Basic
    @Column(name = "is_ai")
    var ai: Boolean? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ChroniclerData
        if (id != that.id) return false
        return if (name != that.name) false else ai == that.ai
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (ai != null) ai.hashCode() else 0
        return result
    }
}
