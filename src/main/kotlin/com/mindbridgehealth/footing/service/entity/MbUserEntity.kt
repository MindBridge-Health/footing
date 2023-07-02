package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "mb_user")
class MbUserEntity: EntityModel() {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    override var id: Int? = null

    @Column(name="alt_id")
    var altId: String? = null

    @Version
    @Column(name = "version")
    var version: Int = 0

    @Basic
    @Column(name = "lastname")
    var lastname: String? = null

    @Basic
    @Column(name = "firstname")
    var firstname: String? = null

    @Basic
    @Column(name = "middlename")
    var middlename: String? = null

    @Basic
    @Column(name = "email")
    var email: String? = null

    @Basic
    @Column(name = "mobile")
    var mobile: String? = null

    @Basic
    @Column(name = "is_active")
    var isActive: Boolean? = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MbUserEntity

        if (id != other.id) return false
        if (version != other.version) return false
        if (lastname != other.lastname) return false
        if (firstname != other.firstname) return false
        if (middlename != other.middlename) return false
        return isActive == other.isActive
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + version
        result = 31 * result + (lastname?.hashCode() ?: 0)
        result = 31 * result + (firstname?.hashCode() ?: 0)
        result = 31 * result + (middlename?.hashCode() ?: 0)
        result = 31 * result + (isActive?.hashCode() ?: 0)
        return result
    }


}
