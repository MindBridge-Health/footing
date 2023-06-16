package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.api.dto.BenefactorCreateDto
import com.mindbridgehealth.footing.api.dto.mapper.BenefactorCreateDtoMapper
import com.mindbridgehealth.footing.service.BenefactorService
import com.mindbridgehealth.footing.service.model.Benefactor
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/benefactors")
class BenefactorController(val service: BenefactorService, val mapper: BenefactorCreateDtoMapper) {

    @GetMapping("/")
    fun get(@AuthenticationPrincipal principal: Jwt): Benefactor {

        val optBenefactor = service.findBenefactorById(principal.subject)
        return optBenefactor.orElseGet { null }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String, @AuthenticationPrincipal principal: Jwt): Benefactor {

        val optBenefactor = service.findBenefactorById(
            Base36Encoder.decodeAltId(id)
        )

        return optBenefactor.orElseGet { null }
    }

    @PostMapping("/")
    fun post(
        @RequestBody benefactorCreateDto: BenefactorCreateDto,
        @AuthenticationPrincipal principal: Jwt
    ): String {
        val altId = principal.subject
        return service.save(mapper.benefactorCreateDtoToBenefactor(benefactorCreateDto), altId)
    }

    @PutMapping("/")
    fun put(@RequestBody benefactor: BenefactorCreateDto, @AuthenticationPrincipal principal: Jwt): Benefactor {
        val altId = principal.subject
        return service.update(mapper.benefactorCreateDtoToBenefactor(benefactor), altId)
    }

    //These operations below expect the original (e.g. not encoded) auth0 user ID
    @PostMapping("/{id}")
    fun postOnBehalf(
        @RequestBody benefactorCreateDto: BenefactorCreateDto,
        @PathVariable id: String,
    ): String {
        return service.save(mapper.benefactorCreateDtoToBenefactor(benefactorCreateDto), id)
    }

    @PutMapping("/{id}")
    fun putOnBehalf(@RequestBody benefactor: BenefactorCreateDto, @PathVariable id: String, ): Benefactor {
        return service.update(mapper.benefactorCreateDtoToBenefactor(benefactor), id)
    }

    @DeleteMapping("/{id}")
    fun deleteOnBehalf(@PathVariable id: String) {
        service.deactivateBenefactor(id)
    }
}
