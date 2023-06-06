package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.MediaEntity
import com.mindbridgehealth.footing.service.model.Media
import org.mapstruct.*
import org.springframework.stereotype.Service
import java.net.URI

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class, StorytellerEntityMapper::class])
abstract class MediaEntityMapper: IdMapper() {

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun entityToModel(mediaEntity: MediaEntity): Media

    @Mapping(source = "location", target = "location", ignore = true)
    @Mapping(source = "id", target = "id", ignore = true)
    abstract fun modelToEntity(media: Media): MediaEntity

    @AfterMapping
    fun afterMappingEntity(source: Media, @MappingTarget target: MediaEntity) {
        target.location = source.location.toString()
    }

    @AfterMapping
    fun afterMappingEntity(source: MediaEntity, @MappingTarget target: Media,) {
        source.location?.let { target.location = URI.create(it) }
    }

}