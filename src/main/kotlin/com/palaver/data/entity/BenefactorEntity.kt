package com.palaver.data.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "benefactor")
@PrimaryKeyJoinColumn(name="id")
class BenefactorEntity : EcUserEntity() {

}
