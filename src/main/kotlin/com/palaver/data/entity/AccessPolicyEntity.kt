package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "access_policy")
class AccessPolicyEntity {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_allowed_resource_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "resource_id")])
    var allowedResources: MutableList<com.palaver.data.entity.ResourceEntity>? = Collections.emptyList()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_denied_resource_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "resource_id")])
    var deniedResources: MutableList<com.palaver.data.entity.ResourceEntity>? = Collections.emptyList()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_user_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "user_id")])
    var policyUserIds: MutableList<com.palaver.data.entity.EcUserEntity>? = Collections.emptyList()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as com.palaver.data.entity.AccessPolicyEntity
        return if (id != that.id) false else name == that.name
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
