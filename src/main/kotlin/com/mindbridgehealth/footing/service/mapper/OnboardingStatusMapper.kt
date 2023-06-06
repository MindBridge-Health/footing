package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.model.OnboardingStatus
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.springframework.stereotype.Service

abstract class OnboardingStatusMapper {

    fun map(value: Int) : OnboardingStatus = OnboardingStatus.getByValue(value) ?: throw Exception()

    fun map(value: OnboardingStatus) : Int = value.value
}