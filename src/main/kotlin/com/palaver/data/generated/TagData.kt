package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tag")
class TagData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    private var name: String? = null

    @Basic
    @Column(name = "text")
    var text: String? = null
    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val tagData = o as TagData
        if (id != tagData.id) return false
        return if (name != tagData.name) false else text == tagData.text
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (text != null) text.hashCode() else 0
        return result
    }
}
