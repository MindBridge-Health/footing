package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "organization")
class OrganizationEntity {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrganizationEntity
        if (if (id != null) id != that.id else that.id != null) return false
        return if (if (name != null) name != that.name else that.name != null) false else true
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
