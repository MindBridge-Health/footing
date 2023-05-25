package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "storyteller")
class StorytellerData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    lateinit var name: String

    @Basic
    @Column(name = "contact_method")
    var contactMethod: String = ""

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "storyteller_benefactor_link", joinColumns = [JoinColumn(name = "storyteller_id")], inverseJoinColumns = [JoinColumn(name = "benefactor_id")])
    var benefactors: MutableList<BenefactorData>? = Collections.emptyList()

    @OneToOne
    @JoinColumn(name = "preferred_chronicler_id", referencedColumnName = "id")
    var preferredChronicler: ChroniclerData? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as StorytellerData
        if (id != that.id) return false
        if (name != that.name) return false
        return contactMethod == that.contactMethod
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (contactMethod != null) contactMethod.hashCode() else 0
        return result
    }
}
