package com.mindbridgehealth.footing.service.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mindbridgehealth.footing.service.entity.MediaEntity
import com.mindbridgehealth.footing.service.model.Media
import com.mindbridgehealth.footing.service.util.Base36Encoder
import net.minidev.json.JSONObject
import org.mapstruct.*
import org.springframework.stereotype.Service
import java.net.URI

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class, StoryEntityMapper::class])
abstract class MediaEntityMapper: IdMapper() {

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "owner", target = "ownerId", ignore = true)
    abstract fun entityToModel(mediaEntity: MediaEntity): Media

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(target = "rawJson", ignore = true)
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    abstract fun modelToEntity(media: Media): MediaEntity

    @AfterMapping
    fun afterMappingEntity(source: Media, @MappingTarget target: MediaEntity) {
        target.location = source.location.toString()
        target.rawJson = jacksonObjectMapper().writeValueAsString(source)
    }

    @AfterMapping
    fun afterMappingModel(source: MediaEntity, @MappingTarget target: Media) {
        source.location?.let { target.location = URI.create(it) }
    }

}