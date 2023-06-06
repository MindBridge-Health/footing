package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.AccessPolicyEntity
import com.mindbridgehealth.footing.service.model.AccessPolicy
import org.mapstruct.Mapper

//@Mapper
interface AccessPolicyEntityMapper {

    fun entityToModel(accessPolicyEntity: AccessPolicyEntity) : AccessPolicy

    fun modelToEntity(accessPolicy: AccessPolicy) : AccessPolicyEntity
}