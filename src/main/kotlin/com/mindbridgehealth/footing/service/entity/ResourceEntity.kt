package com.mindbridgehealth.footing.service.entity

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
    @Column(name = "alt_id")
    override var altId: String? = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

    @Basic
    @Column(name = "name")
    var name: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name="resource_tag_link", joinColumns = [JoinColumn(name = "resource_id")], inverseJoinColumns = [JoinColumn(name = "tag_id")])
    var tags: MutableList<TagEntity>? = Collections.emptyList()

    @Basic
    @Column(name = "is_deleted")
    var isDeleted: Boolean? = false

    @ManyToOne
    @JoinColumn(name = "owner")
    var owner: MbUserEntity? = null


    constructor(name: String?, altId: String?) : this() {
        this.name = name
        this.altId = altId ?: UUID.randomUUID().toString().replace("-", "").substring(0, 8)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResourceEntity

        if (id != other.id) return false
        if (altId != other.altId) return false
        if (name != other.name) return false
        if (tags != other.tags) return false
        if (isDeleted != other.isDeleted) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (altId?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (tags?.hashCode() ?: 0)
        result = 31 * result + (isDeleted?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)
        return result
    }
}
