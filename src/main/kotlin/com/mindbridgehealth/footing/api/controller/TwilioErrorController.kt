package com.mindbridgehealth.footing.api.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/error")
class TwilioErrorController {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @PostMapping
    fun post(@RequestBody body: Any) {
        logger.error(body.toString())
    }
}