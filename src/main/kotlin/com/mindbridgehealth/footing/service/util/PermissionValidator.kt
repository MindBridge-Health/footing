package com.mindbridgehealth.footing.service.util

import com.mindbridgehealth.footing.service.model.Resource
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.client.HttpClientErrorException

object PermissionValidator {

    fun assertValidPermissions(
        resource: Resource,
        principal: Jwt,
        userId: String? = principal.subject
    ) {
        val permissions = principal.claims["permissions"] as List<*>?
        if ((permissions == null || !permissions.contains("read:userdata")) && (resource.ownerId != null && resource.ownerId != null && principal.subject != resource.ownerId && userId != resource.ownerId )) {
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
    }
}