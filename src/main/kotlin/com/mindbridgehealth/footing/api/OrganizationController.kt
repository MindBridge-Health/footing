package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.service.OrganizationService
import com.mindbridgehealth.footing.service.model.Organization
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizations")
class OrganizationController(val organizationService: OrganizationService) {

    @PostMapping("/")
    fun post(@RequestBody organization: Organization): Organization {
        return organizationService.save(organization)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Organization {
        return organizationService.findById(id)
    }

}