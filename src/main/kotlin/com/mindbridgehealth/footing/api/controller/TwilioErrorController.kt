package com.mindbridgehealth.footing.api.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/error")
class TwilioErrorController(val spaController: SpaController) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @PostMapping(consumes = ["application/x-www-form-urlencoded"])
    fun post(@RequestBody body: String) {
        logger.error(body)
    }

    @GetMapping
    fun get(): ModelAndView {
        return ModelAndView("forward:/index.html")
    }
}