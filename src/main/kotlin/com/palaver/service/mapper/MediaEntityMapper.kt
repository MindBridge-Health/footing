package com.palaver.service.mapper

import com.palaver.data.entity.MediaEntity
import com.palaver.service.model.Media
import org.mapstruct.*
import java.net.URI

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class MediaEntityMapper {

    @Mapping(source = "location", target = "location", ignore = true)
    abstract fun entityToModel(mediaEntity: MediaEntity): Media

    @Mapping(source = "location", target = "location", ignore = true)
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