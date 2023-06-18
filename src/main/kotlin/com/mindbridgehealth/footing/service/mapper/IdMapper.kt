package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.EntityModel
import com.mindbridgehealth.footing.data.entity.MbUserEntity
import com.mindbridgehealth.footing.data.entity.ResourceEntity
import com.mindbridgehealth.footing.service.model.DataModel
import com.mindbridgehealth.footing.service.model.User
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.MappingTarget

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
    fun entityToModelId(source: EntityModel, @MappingTarget target: DataModel) {
        target.id = Base36Encoder.encode(source.id.toString())
    }

    @AfterMapping
    fun modelToEntityId(source: DataModel, @MappingTarget target: EntityModel) {
        val id = source.id
        target.id = if(id != null) Base36Encoder.decode(id).toInt() else null
    }
}