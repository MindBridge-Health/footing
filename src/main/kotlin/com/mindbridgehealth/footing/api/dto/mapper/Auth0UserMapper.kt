package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.Auth0User
import com.mindbridgehealth.footing.service.entity.MbUserEntity
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface Auth0UserMapper {

    @Mapping(source = "userId", target = "altId")
    @Mapping(source = "givenName", target = "firstname")
    @Mapping(source = "familyName", target = "lastname")
    @Mapping(source = "phoneNumber", target = "mobile")
    fun auth0UserToMbUserEntity(source: Auth0User): MbUserEntity
}