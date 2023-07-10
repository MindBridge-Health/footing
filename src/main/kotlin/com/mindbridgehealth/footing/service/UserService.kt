package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.api.dto.Auth0User
import com.mindbridgehealth.footing.api.dto.mapper.Auth0UserMapper
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(val userDb: MindBridgeUserRepository, val userMapper: Auth0UserMapper) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(auth0User: Auth0User) {
        val entity = userMapper.auth0UserToMbUserEntity(auth0User)
        val altId = entity.altId ?: throw Exception("Logged in user had null altId")
        if(userDb.findByAltId(altId).isEmpty) {
            logger.debug("Creating User")
            userDb.save(entity)
        } else {
            logger.debug("User already exists")
        }

    }
}