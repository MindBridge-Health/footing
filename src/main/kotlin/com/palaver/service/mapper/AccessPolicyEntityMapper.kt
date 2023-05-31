package com.palaver.service.mapper

import com.palaver.data.entity.AccessPolicyEntity
import com.palaver.service.model.AccessPolicy
import org.mapstruct.Mapper

//@Mapper
interface AccessPolicyEntityMapper {

    fun entityToModel(accessPolicyEntity: AccessPolicyEntity) : AccessPolicy

    fun modelToEntity(accessPolicy: AccessPolicy) : AccessPolicyEntity
}