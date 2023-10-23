package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.api.dto.StorytellerListEntry
import com.mindbridgehealth.footing.api.dto.mapper.StorytellerCreateDtoMapper
import com.mindbridgehealth.footing.api.dto.mapper.StorytellerListEntryDtoMapper
import com.mindbridgehealth.footing.service.StorytellerService
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatusCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/v1/storytellers")
class StorytellerController(val service: StorytellerService, val mapper: StorytellerCreateDtoMapper, val storytellerListEntryDtoMapper: StorytellerListEntryDtoMapper) {

    @GetMapping("/all/")
    fun getAll(@AuthenticationPrincipal principal: Jwt): List<StorytellerListEntry> {
        val storytellers = service.getAllStorytellers()
        return storytellers.map {
            storytellerListEntryDtoMapper.storytellerToStorytellerListEntry(it)
        }
    }

    @GetMapping("/")
    fun get(@AuthenticationPrincipal principal: Jwt): Storyteller {
        val optStoryteller = service.findStorytellerByAltId(principal.subject)
        return optStoryteller.getOrElse { throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found") }
    }

    @GetMapping("/{id}")
    fun getOnBehalf(@PathVariable id: String, @AuthenticationPrincipal principal: Jwt): Storyteller {
        val optStoryteller = service.findStorytellerByAltId(Base36Encoder.decodeAltId(id))
        return optStoryteller.getOrElse { throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found") }
    }

    @PostMapping("/")
    fun post(
        @RequestBody storytellerCreateDto: StorytellerCreateDto,
        @AuthenticationPrincipal principal: Jwt
    ): Storyteller {
        val altId = principal.subject
        return service.save(mapper.storytellerCreateDtoToStoryteller(storytellerCreateDto), altId)
    }

    @PostMapping("/{id}")
    fun postOnBehalf(
        @RequestBody storytellerCreateDto: StorytellerCreateDto,
        @PathVariable id: String,
        @RequestParam idEncoded: Boolean? = false,
    ): Storyteller {
        val altId = if(idEncoded == true) Base36Encoder.decodeAltId(id) else id
        return service.save(mapper.storytellerCreateDtoToStoryteller(storytellerCreateDto), altId)
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: StorytellerCreateDto, @AuthenticationPrincipal principal: Jwt): Storyteller {
        val altId = principal.subject
        return service.update(mapper.storytellerCreateDtoToStoryteller(storyteller), altId)
    }

    @PutMapping("/{id}")
    fun putOnBehalf(@RequestBody storyteller: Storyteller,@PathVariable id: String,): Storyteller {
        return service.update(storyteller, Base36Encoder.decodeAltId(id))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String, @RequestParam force: Boolean? = false) {
        if(force == true) {
            service.hardDeleteStoryteller(Base36Encoder.decodeAltId(id))
        } else {
            service.deactivateStoryteller(Base36Encoder.decodeAltId(id))
        }
    }

}