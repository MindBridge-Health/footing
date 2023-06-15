package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.api.dto.mapper.StorytellerCreateDtoMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.StorytellerService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/storytellers")
class StorytellerController(val service: StorytellerService, val mapper: StorytellerCreateDtoMapper) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String) = service.findStorytellerById(id)

    @PostMapping("/")
    fun onboardingSubmit(@RequestBody storytellerCreateDto: StorytellerCreateDto, @AuthenticationPrincipal principal: Jwt): Storyteller {
        val altId = principal.subject
        val update = service.update(mapper.storytellerCreateDtoToStoryteller(storytellerCreateDto), altId)
        return update
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: StorytellerCreateDto, @AuthenticationPrincipal principal: Jwt): Storyteller {
        val altId = principal.subject
        return service.update(mapper.storytellerCreateDtoToStoryteller(storyteller), altId)
    }

    @DeleteMapping("/{id}")
    fun deleteStoryteller(@PathVariable id: String) {
        service.deactivateStoryteller(id)
    }

}