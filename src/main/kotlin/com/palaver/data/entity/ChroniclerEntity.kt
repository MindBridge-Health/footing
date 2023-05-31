package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "chronicler")
@PrimaryKeyJoinColumn(name="id")
class ChroniclerEntity: EcUserEntity() {
    @Basic
    @Column(name = "is_ai")
    var ai: Boolean? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ChroniclerEntity

        return ai == other.ai
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (ai?.hashCode() ?: 0)
        return result
    }
}
