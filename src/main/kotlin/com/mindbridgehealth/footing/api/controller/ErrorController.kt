package com.mindbridgehealth.footing.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView

@Controller
class ErrorController {

    @GetMapping("/fwd")
    fun handleError(): ModelAndView {
        return ModelAndView("forward:/index.html")
    }
}
