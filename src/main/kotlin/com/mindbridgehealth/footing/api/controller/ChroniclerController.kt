package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.service.ChroniclerService
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatusCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID

@RestController
@RequestMapping("/api/v1/chroniclers")
class ChroniclerController(val chroniclerService: ChroniclerService, val mapper: ChroniclerEntityMapper) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Chronicler = chroniclerService.findChroniclerById(id).orElseThrow()

    @PostMapping("/")
    fun post(
        @RequestBody chronicler: Chronicler,
        @AuthenticationPrincipal principal: Jwt
    ): String {
        val altId = principal.subject
        return chroniclerService.save(chronicler, altId).id ?: throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found")
    }


    @PostMapping("/{id}")
    fun postOnBehalf(
        @RequestBody chronicler: Chronicler,
        @PathVariable id: String
    ): String {
        return chroniclerService.save(chronicler, Base36Encoder.decodeAltId(id)).id ?: throw HttpClientErrorException(
            HttpStatusCode.valueOf(404), "Not Found")
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        chroniclerService.deactivateChronicler(id)
    }
}