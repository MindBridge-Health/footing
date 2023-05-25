package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "organization")
class OrganizationData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: Any? = null
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as OrganizationData
        if (if (id != null) id != that.id else that.id != null) return false
        return if (if (name != null) name != that.name else that.name != null) false else true
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
