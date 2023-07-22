package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.OrganizationService
import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/organizations")
class OrganizationController(val organizationService: OrganizationService) {

    @PostMapping("/")
    fun post(@RequestBody organization: Organization): Organization {
        return organizationService.save(organization)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Organization {
        return organizationService.findByAltId(Base36Encoder.decodeAltId(id))
    }

}