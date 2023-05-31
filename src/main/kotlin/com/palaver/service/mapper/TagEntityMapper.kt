package com.palaver.service.mapper

import com.palaver.data.entity.TagEntity
import com.palaver.service.model.Tag
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TagEntityMapper {

    fun entityToModel(tagEntity: TagEntity): Tag

    fun modelToEntity(tag: Tag): TagEntity
}