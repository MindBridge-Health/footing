package com.mindbridgehealth.footing.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "resource")
abstract class ResourceEntity(): EntityModel() {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    override var id: Int? = null

    @Basic
    @Column(name = "name")
    var name: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name="resource_tag_link", joinColumns = [JoinColumn(name = "resource_id")], inverseJoinColumns = [JoinColumn(name = "tag_id")])
    var tags: MutableList<TagEntity>? = Collections.emptyList()

    @Basic
    @Column(name = "is_deleted")
    var isDeleted: Boolean? = false

    constructor(name: String?) : this() {
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ResourceEntity
        return if (id != that.id) false else name == that.name
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
