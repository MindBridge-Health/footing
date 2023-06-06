package com.palaver.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "benefactor")
@PrimaryKeyJoinColumn(name="id")
class BenefactorEntity : MbUserEntity() {

}
