package com.palaver.api

import com.palaver.service.model.Storyteller
import com.palaver.service.StorytellerService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/storytellers")
class StorytellerController(val service: StorytellerService) {
    @GetMapping("/")
    fun index(): List<Storyteller> = service.findStorytellers()

    @PostMapping("/")
    fun post(@RequestBody storyteller: Storyteller): UUID {
        return service.save(storyteller)
    }

    @PutMapping("/")
    fun put(@RequestBody storyteller: Storyteller) {
        service.update(storyteller)
    }


    @DeleteMapping("/{id}")
    fun deleteStoryteller(@PathVariable id: UUID) {
        service.deleteStoryteller(id)
    }
    @DeleteMapping("/all")
    fun deleteAll() {
        service.deleteAll()
    }

}