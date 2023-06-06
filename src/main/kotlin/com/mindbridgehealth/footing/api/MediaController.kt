package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.service.MediaService
import com.mindbridgehealth.footing.service.model.Media
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/media")
class MediaController(val mediaService: MediaService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String) = mediaService.findMediaById(id)
    @PostMapping("/storytellers/{id}")
    fun postToStoryteller(@RequestBody media: Media, @PathVariable id: String): String = mediaService.associateMediaWithStoryteller(media, id)

    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: String) {
        mediaService.deleteMedia(id)    }

//    @PostMapping("/story/{id}")
//    fun postToStory(media: Media, @PathVariable id: UUID) = mediaService.catalogMedia(media, id)
}