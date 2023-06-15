package com.mindbridgehealth.footing.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Auth0User(
    @JsonProperty("app_metadata") val appMetadata: Map<String, Any>?,
    @JsonProperty("created_at") val createdAt: String?,
    @JsonProperty("email") val email: String?,
    @JsonProperty("email_verified") val emailVerified: Boolean?,
    @JsonProperty("family_name") val familyName: String?,
    @JsonProperty("given_name") val givenName: String?,
    @JsonProperty("last_password_reset") val lastPasswordReset: String?,
    @JsonProperty("multifactor") val multifactor: List<String>?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("nickname") val nickname: String?,
    @JsonProperty("phone_number") val phoneNumber: String?,
    @JsonProperty("phone_verified") val phoneVerified: Boolean?,
    @JsonProperty("picture") val picture: String?,
    @JsonProperty("updated_at") val updatedAt: String?,
    @JsonProperty("user_id") val userId: String?,
    @JsonProperty("user_metadata") val userMetadata: Map<String, Any>?,
    @JsonProperty("username") val username: String?
)
