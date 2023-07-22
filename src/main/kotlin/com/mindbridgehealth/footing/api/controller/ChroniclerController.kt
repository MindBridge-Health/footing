package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.ChroniclerCreateDto
import com.mindbridgehealth.footing.api.dto.mapper.ChroniclerCreateDtoMapper
import com.mindbridgehealth.footing.service.ChroniclerService
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatusCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RestController
@RequestMapping("/api/v1/chroniclers")
class ChroniclerController(val chroniclerService: ChroniclerService, val mapper: ChroniclerCreateDtoMapper) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Chronicler = chroniclerService.findChroniclerByAltId(Base36Encoder.decodeAltId(id)).orElseThrow()

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
        chroniclerService.deactivateChronicler(Base36Encoder.decodeAltId(id))
    }

    @PutMapping("/{id}")
    fun putOnBehalf(@RequestBody chronicler: ChroniclerCreateDto, @PathVariable id: String, ): Chronicler {
        return chroniclerService.update(mapper.chroniclerCreateDtoToChronicler(chronicler), Base36Encoder.decodeAltId(id))
    }
    
}