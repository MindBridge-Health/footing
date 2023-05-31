package com.palaver.service.mapper

import com.palaver.data.entity.MediaEntity
import com.palaver.service.model.Media
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MediaEntityMapper {

    fun entityToModel(mediaEntity: MediaEntity): Media

    fun modelToEntity(media: Media): MediaEntity
}