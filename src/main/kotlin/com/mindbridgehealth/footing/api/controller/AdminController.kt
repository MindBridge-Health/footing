package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.AdminService
import com.mindbridgehealth.footing.service.StorytellerService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(private val adminService: AdminService, private val storytellerService: StorytellerService) {

    @PostMapping("/sendUrl/{sid}")
    fun sendUrl(@PathVariable sid: String) {
        val storyteller = storytellerService.findStorytellerByAltId(Base36Encoder.decodeAltId(sid)).getOrElse { throw Exception("Didn't find storyteller") }
        adminService.sendHomeLinkToUser(storyteller)
    }
}