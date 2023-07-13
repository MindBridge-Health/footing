package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.EntityModel
import com.mindbridgehealth.footing.service.entity.MbUserEntity
import com.mindbridgehealth.footing.service.entity.ResourceEntity
import com.mindbridgehealth.footing.service.model.DataModel
import com.mindbridgehealth.footing.service.model.Resource
import com.mindbridgehealth.footing.service.model.User
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.MappingTarget
import java.util.*

abstract class IdMapper {
    @AfterMapping
    fun calledWithSourceAndTarget(source: MbUserEntity, @MappingTarget target: User){
        //target.onboardingStatus = OnboardingStatus.getByValue(source.onboardingStatus!!)
        target.id = if(source.altId.isNullOrEmpty()) null else Base36Encoder.encodeAltId(source.altId!!)
    }

    @AfterMapping
    fun calledWithSourceAndTarget(source: User, @MappingTarget target: MbUserEntity){
        //target.onboardingStatus = source.onboardingStatus?.value
        target.altId = if(source.id.isNullOrEmpty()) null else Base36Encoder.decodeAltId(source.id!!)
    }

    @AfterMapping
    fun entityToModelId(source: ResourceEntity, @MappingTarget target: Resource) {
        target.id = Base36Encoder.encodeAltId(source.id.toString())
        target.id = if(source.altId != null)
            Base36Encoder.encodeAltId(source.altId.toString())
        else
            Base36Encoder.encodeAltId(source.id.toString())
    }

    @AfterMapping
    fun modelToEntityId(source: Resource, @MappingTarget target: ResourceEntity) {
        target.altId = if (source.id != null)
            Base36Encoder.decodeAltId(source.id!!)
        else
            UUID.randomUUID().toString().replace("-", "").substring(0, 8)
    }
}