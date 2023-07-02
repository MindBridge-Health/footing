package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.model.PreferredTime
import org.mapstruct.AfterMapping
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import java.time.DayOfWeek

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class PreferredTimeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storyteller", ignore = true)
    abstract fun modelToEntity(preferredTime: PreferredTime): PreferredTimeEntity

    @Mapping(target = "storyteller", ignore = true)
    abstract fun entityToModel(preferredTimeEntity: PreferredTimeEntity): PreferredTime

    @AfterMapping
    fun calledWithSourceAndTarget(source: PreferredTime, @MappingTarget target: PreferredTimeEntity) {
        target.dayOfWeek = source.dayOfWeek.name
    }

    @AfterMapping
    fun calledWithSourceAndTarget(source: PreferredTimeEntity, @MappingTarget target: PreferredTime) {
        target.dayOfWeek = DayOfWeek.valueOf(source.dayOfWeek!!.uppercase())
    }
}