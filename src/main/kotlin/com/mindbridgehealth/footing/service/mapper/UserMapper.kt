package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.MbUserEntity
import com.mindbridgehealth.footing.service.model.User
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.AfterMapping
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.FIELD,
    uses = [OrganizationEntityMapper::class]
)
@Component
class UserMapper() {

    constructor(organizationEntityMapper: OrganizationEntityMapper) : this() {
        this.organizationEntityMapper = organizationEntityMapper
    }
    @Autowired
    var organizationEntityMapper: OrganizationEntityMapper? = null

    @AfterMapping
    fun calledWithSourceAndTarget(source: MbUserEntity, @MappingTarget target: User){
        //target.onboardingStatus = OnboardingStatus.getByValue(source.onboardingStatus!!)
        target.id = if(source.altId.isNullOrEmpty()) null else Base36Encoder.encodeAltId(source.altId!!)
        target.organization = source.organization?.let { organizationEntityMapper?.entityToModel(it) }
    }

    @AfterMapping
    fun calledWithSourceAndTarget(source: User, @MappingTarget target: MbUserEntity){
        //target.onboardingStatus = source.onboardingStatus?.value
        target.altId = if(source.id.isNullOrEmpty()) null else Base36Encoder.decodeAltId(source.id!!)
        target.organization = source.organization?.let { organizationEntityMapper?.modelToEntity(it) }
    }

}