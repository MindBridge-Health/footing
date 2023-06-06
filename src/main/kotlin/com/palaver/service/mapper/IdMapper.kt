package com.palaver.service.mapper

import com.palaver.data.entity.EntityModel
import com.palaver.service.model.DataModel
import com.palaver.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.MappingTarget

abstract class IdMapper {
    @AfterMapping
    fun calledWithSourceAndTarget(source: EntityModel, @MappingTarget target: DataModel){
        //target.onboardingStatus = OnboardingStatus.getByValue(source.onboardingStatus!!)
        target.id = Base36Encoder.encode(source.id.toString())
    }

    @AfterMapping
    fun calledWithSourceAndTarget(source: DataModel, @MappingTarget target: EntityModel){
        //target.onboardingStatus = source.onboardingStatus?.value
        target.id = if(source.id.isNullOrEmpty()) -1 else Base36Encoder.decode(source.id!!).toInt()
    }
}