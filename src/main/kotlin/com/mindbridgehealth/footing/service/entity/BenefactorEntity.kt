package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*

@Entity
@Table(name = "benefactor")
@PrimaryKeyJoinColumn(name="id")
class BenefactorEntity : MbUserEntity() {

}
