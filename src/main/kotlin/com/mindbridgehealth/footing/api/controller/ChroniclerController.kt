package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.ChroniclerService
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/chroniclers")
class ChroniclerController(val chroniclerService: ChroniclerService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Chronicler = chroniclerService.findChroniclerById(id).orElseThrow()

    @PostMapping("/")
    fun post(@RequestBody chronicler: Chronicler) = chroniclerService.save(chronicler)


    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        chroniclerService.deactivateChronicler(id)
    }
}