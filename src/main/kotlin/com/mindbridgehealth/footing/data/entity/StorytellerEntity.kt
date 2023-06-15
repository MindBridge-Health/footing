package com.mindbridgehealth.footing.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "storyteller")
@PrimaryKeyJoinColumn(name="id")
class StorytellerEntity: MbUserEntity() {
    @Basic
    @Column(name = "contact_method")
    var contactMethod: String? = ""

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "storyteller_benefactor_link", joinColumns = [JoinColumn(name = "storyteller_id")], inverseJoinColumns = [JoinColumn(name = "benefactor_id")])
    var benefactors: MutableList<BenefactorEntity>? = Collections.emptyList()

    @OneToOne
    @JoinColumn(name = "preferred_chronicler_id", referencedColumnName = "id")
    var preferredChronicler: ChroniclerEntity? = null

    @Basic
    @Column(name = "onboarding_status")
    var onboardingStatus: Int? = 0

    @OneToMany(mappedBy = "storyteller")
    var preferredTimes: MutableList<PreferredTimeEntity>? = Collections.emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StorytellerEntity

        if (contactMethod != other.contactMethod) return false
        if (benefactors != other.benefactors) return false
        if (preferredChronicler != other.preferredChronicler) return false
        return onboardingStatus == other.onboardingStatus
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + contactMethod.hashCode()
        result = 31 * result + (benefactors?.hashCode() ?: 0)
        result = 31 * result + (preferredChronicler?.hashCode() ?: 0)
        result = 31 * result + onboardingStatus!!
        return result
    }


}
