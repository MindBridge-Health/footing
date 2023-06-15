package com.mindbridgehealth.footing

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class HomeController {
    @GetMapping("/")
    fun home(model: Model, @AuthenticationPrincipal principal: OidcUser?): String {
        if (principal != null) {
            model.addAttribute("profile", principal.claims)
            model.addAttribute("jwt", principal.idToken.tokenValue)
        }
        return "index"
    }
}