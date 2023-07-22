package com.mindbridgehealth.footing.service.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "organization")
@PrimaryKeyJoinColumn(name="id")
class OrganizationEntity: ResourceEntity()
