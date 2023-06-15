package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.EntityModel
import com.mindbridgehealth.footing.service.model.DataModel
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.MappingTarget

abstract class IdMapper {
    @AfterMapping
    fun calledWithSourceAndTarget(source: EntityModel, @MappingTarget target: DataModel){
        //target.onboardingStatus = OnboardingStatus.getByValue(source.onboardingStatus!!)
        target.id = if(source.altId.isNullOrEmpty()) null else Base36Encoder.encodeAltId(source.altId!!)
    }

    @AfterMapping
    fun calledWithSourceAndTarget(source: DataModel, @MappingTarget target: EntityModel){
        //target.onboardingStatus = source.onboardingStatus?.value
        target.altId = if(source.id.isNullOrEmpty()) null else Base36Encoder.decodeAltId(source.id!!)
    }
}