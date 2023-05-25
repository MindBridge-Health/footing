package com.palaver.api

import com.palaver.service.model.Storyteller
import com.palaver.service.StorytellerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/storytellers")
class StorytellerController(val service: StorytellerService) {
    @GetMapping("/")
    fun index(): List<Storyteller> = service.findStorytellers()

    @PostMapping("/")
    fun post(@RequestBody storyteller: Storyteller) {
        service.save(storyteller)
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: Storyteller) {
        service.update(storyteller)
    }

    @DeleteMapping("/all")
    fun deleteAll() {
        service.deleteAll()
    }

}