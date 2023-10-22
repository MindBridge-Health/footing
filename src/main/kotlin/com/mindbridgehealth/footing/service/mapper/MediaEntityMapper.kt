package com.mindbridgehealth.footing.service.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mindbridgehealth.footing.service.entity.MediaEntity
import com.mindbridgehealth.footing.service.model.Media
import org.mapstruct.*
import java.net.URI

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class, StoryEntityMapper::class])
abstract class MediaEntityMapper: IdMapper() {

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "owner", target = "ownerId", ignore = true)
    @Mapping(source = "thumbnail", target = "thumbnail", ignore = true)
    abstract fun entityToModel(mediaEntity: MediaEntity): Media

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(target = "rawJson", ignore = true)
    @Mapping(source = "ownerId", target = "owner", ignore = true)
    @Mapping(source = "thumbnail", target = "thumbnail", ignore = true)
    abstract fun modelToEntity(media: Media): MediaEntity

    @AfterMapping
    fun afterMappingEntity(source: Media, @MappingTarget target: MediaEntity) {
        target.location = source.location.toString()
        target.thumbnail = source.thumbnail.toString()
        target.rawJson = jacksonObjectMapper().writeValueAsString(source)
    }

    @AfterMapping
    fun afterMappingModel(source: MediaEntity, @MappingTarget target: Media) {
        source.location?.let { target.location = URI.create(it) }
        source.thumbnail?.let { target.thumbnail = URI.create(it) }
    }

}