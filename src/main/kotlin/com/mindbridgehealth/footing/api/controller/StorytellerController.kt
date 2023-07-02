package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.api.dto.mapper.StorytellerCreateDtoMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.StorytellerService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatusCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.HttpStatusCodeException
import java.util.*

@RestController
@RequestMapping("/api/v1/storytellers")
class StorytellerController(val service: StorytellerService, val mapper: StorytellerCreateDtoMapper) {

    @GetMapping("/")
    fun get(@AuthenticationPrincipal principal: Jwt): Storyteller {

        val optStoryteller =service.findStorytellerById(principal.subject)
        return optStoryteller.orElseGet { null }
    }
    @GetMapping("/{id}")
    fun get(@PathVariable id: String, @AuthenticationPrincipal principal: Jwt): Storyteller {

       val optStoryteller = service.findStorytellerById(
            Base36Encoder.decodeAltId(id))

        return optStoryteller.orElseGet { null }
    }

    @PostMapping("/")
    fun post(
        @RequestBody storytellerCreateDto: StorytellerCreateDto,
        @AuthenticationPrincipal principal: Jwt
    ): String {
        val altId = principal.subject
        return service.save(mapper.storytellerCreateDtoToStoryteller(storytellerCreateDto), altId).id ?: throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found")
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: StorytellerCreateDto, @AuthenticationPrincipal principal: Jwt): Storyteller {
        val altId = principal.subject
        return service.update(mapper.storytellerCreateDtoToStoryteller(storyteller), altId)
    }

    //These operations below expect the original (e.g. not encoded) auth0 user ID
    @PostMapping("/{id}")
    fun postOnBehalf(
        @RequestBody storytellerCreateDto: StorytellerCreateDto,
        @PathVariable id: String,
    ): String {
        return service.save(mapper.storytellerCreateDtoToStoryteller(storytellerCreateDto), id).id ?: throw HttpServerErrorException(HttpStatusCode.valueOf(500))
    }

    @PutMapping("/{id}")
    fun putOnBehalf(@RequestBody storyteller: StorytellerCreateDto,@PathVariable id: String,): Storyteller {
        return service.update(mapper.storytellerCreateDtoToStoryteller(storyteller), id)
    }

    @DeleteMapping("/{id}")
    fun deleteOnBehalf(@PathVariable id: String) {
        service.deactivateStoryteller(id)
    }

}