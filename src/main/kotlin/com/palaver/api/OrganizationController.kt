package com.palaver.api

import com.palaver.service.OrganizationService
import com.palaver.service.model.Organization
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/organizations")
class OrganizationController(val organizationService: OrganizationService) {

    @RequestMapping("/")
    fun post(@RequestBody organization: Organization): Organization {
        return organizationService.save(organization)
    }

    @RequestMapping("/{id}")
    fun get(@PathVariable id: String): Organization {
        return organizationService.findById(id)
    }

}