package com.mindbridgehealth.footing;

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig() {

    @Value("\${auth0.audience}")
    private val audience: String? = null

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String? = null

    private lateinit var logoutHandler: LogoutHandler

    constructor(logoutHandler: LogoutHandler?) : this() {
        this.logoutHandler = logoutHandler!!
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            authorize -> authorize
                .requestMatchers("/", "/images/**").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login()
        }
            .cors().and().oauth2ResourceServer{oauth -> oauth.jwt { jwt -> jwt.decoder(jwtDecoder())}}
//            .logout { logout -> logout.logoutUrl("/logout").addLogoutHandler(logoutHandler)}
//        http.authorizeHttpRequests() // allow all users to access the home pages and the static images directory
//            .requestMatchers("/", "/images/**").permitAll() // all other requests must be authenticated
//            .oauth2ResourceServer((oauth2ResourceServer) ->
//        // works, but not as clear:
//        // oauth2ResourceServer.jwt());
//        oauth2ResourceServer.jwt(jwt -> jwt.decoder(jwtDecoder())));
//            .anyRequest().authenticated()
//            .and().oauth2Login()
//            .and().logout() // handle logout requests at /logout path
//            .logoutRequestMatcher(AntPathRequestMatcher("/logout")) // customize logout handler to log out of Auth0
//            .addLogoutHandler(logoutHandler)
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder? {
        /*
        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
        indeed intended for our app. Adding our own validator is easy to do:
        */
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation<JwtDecoder>(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience!!)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}