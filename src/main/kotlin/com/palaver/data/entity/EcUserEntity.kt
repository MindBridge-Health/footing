package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ec_user")
abstract class EcUserEntity {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EcUserEntity

        if (id != other.id) return false
        if (lastname != other.lastname) return false
        if (firstname != other.firstname) return false
        return middlename == other.middlename
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (lastname?.hashCode() ?: 0)
        result = 31 * result + (firstname?.hashCode() ?: 0)
        result = 31 * result + (middlename?.hashCode() ?: 0)
        return result
    }
}
