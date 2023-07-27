package com.mindbridgehealth.footing.configuration;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.*
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val resourceServerProps: OAuth2ResourceServerProperties,
    private val applicationProps: ApplicationProperties,
    private val signatureValidationFilter: SignatureValidationFilter
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()// I don't think we need CSRF as we're using other forms to validate the request
            .addFilterBefore(signatureValidationFilter, BasicAuthenticationFilter::class.java) // Add the custom filter before other filters
            .authorizeHttpRequests()
            .requestMatchers("/api/v1/health/**", "/api/v1/media*/**", "/api/v1/stories/**","/", "/images/**", "/error*", "/userhome*", "/assets/*", "/index*").permitAll()
            .requestMatchers("/api/v1/storytellers/{id}").hasAuthority("SCOPE_read:userdata")
            .anyRequest().authenticated()
            .and().cors()
            .and().oauth2ResourceServer().jwt()
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder? {
        val issuer = resourceServerProps.jwt.issuerUri
        val decoder = JwtDecoders.fromIssuerLocation<NimbusJwtDecoder>(issuer)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val tokenValidator = DelegatingOAuth2TokenValidator(withIssuer, this::withAudience)
        decoder.setJwtValidator(tokenValidator)
        return decoder
    }

    private fun withAudience(token: Jwt): OAuth2TokenValidatorResult? {
        val audienceError = OAuth2Error(
            OAuth2ErrorCodes.INVALID_TOKEN,
            "The token was not issued for the given audience",
            "https://datatracker.ietf.org/doc/html/rfc6750#section-3.1"
        )
        return if (token.audience.contains(applicationProps.audience)) OAuth2TokenValidatorResult.success() else OAuth2TokenValidatorResult.failure(
            audienceError
        )
    }
}