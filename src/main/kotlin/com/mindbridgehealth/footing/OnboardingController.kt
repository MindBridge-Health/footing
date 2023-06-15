package com.mindbridgehealth.footing

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.api.dto.mapper.StorytellerCreateDtoMapper
import com.mindbridgehealth.footing.service.StorytellerService
import com.mindbridgehealth.footing.service.model.Storyteller
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class OnboardingController(val service: StorytellerService, val mapper: StorytellerCreateDtoMapper) {

    @GetMapping("/onboarding")
    fun onboarding(@AuthenticationPrincipal principal: OidcUser?, model: Model): String {
        if (principal != null) {
            model.addAttribute("profile", principal.claims)
            model.addAttribute("jwt", principal.idToken.tokenValue)
        }
        return "onboarding"
    }

}