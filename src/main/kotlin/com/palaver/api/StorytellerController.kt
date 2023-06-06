package com.palaver.api

import com.palaver.service.model.Storyteller
import com.palaver.service.StorytellerService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/storytellers")
class StorytellerController(val service: StorytellerService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String) = service.findStorytellerById(id)

    @PostMapping("/")
    fun post(@RequestBody storyteller: Storyteller): String {
        return service.save(storyteller)
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: Storyteller) {
        service.update(storyteller)
    }

    @DeleteMapping("/{id}")
    fun deleteStoryteller(@PathVariable id: String) {
        service.deactivateStoryteller(id)
    }

}