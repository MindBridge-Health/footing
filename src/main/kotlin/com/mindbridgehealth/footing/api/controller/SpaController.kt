package com.mindbridgehealth.footing.api.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class SpaController {

    @RequestMapping("/mgmtconsole/{path:(?!index)}**")
    fun forwardToIndex(): ModelAndView {
        println("forwarding")
        return ModelAndView("forward:/mgmtconsole/index.html")
    }
}
