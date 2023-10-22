package com.mindbridgehealth.footing.api.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class SpaController {

    @RequestMapping("/console/(?!index)**")
    fun forwardToIndex(): ModelAndView {
        return ModelAndView("forward:/console/index.html")
    }

    @RequestMapping("/console")
    fun forwardBlankToIndex(): ModelAndView {
        return ModelAndView("forward:/console/index.html")
    }
}
