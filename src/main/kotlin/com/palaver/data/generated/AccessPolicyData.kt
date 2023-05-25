package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "access_policy")
class AccessPolicyData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    var name: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_allowed_resource_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "resource_id")])
    var allowedResources: MutableList<com.palaver.data.generated.ResourceData>? = Collections.emptyList()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_denied_resource_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "resource_id")])
    var deniedResources: MutableList<com.palaver.data.generated.ResourceData>? = Collections.emptyList()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_policy_user_link", joinColumns = [JoinColumn(name = "access_policy_id")], inverseJoinColumns = [JoinColumn(name = "user_id")])
    var policyUserIds: MutableList<com.palaver.data.generated.EcUserData>? = Collections.emptyList()
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as com.palaver.data.generated.AccessPolicyData
        return if (id != that.id) false else name == that.name
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        return result
    }
}
