package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.api.dto.Auth0User
import com.mindbridgehealth.footing.api.dto.mapper.Auth0UserMapper
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userDb: MindBridgeUserRepository, val userMapper: Auth0UserMapper) {

    fun createUser(auth0User: Auth0User) {
        val entity = userMapper.auth0UserToMbUserEntity(auth0User)
        userDb.save(entity)
    }
}