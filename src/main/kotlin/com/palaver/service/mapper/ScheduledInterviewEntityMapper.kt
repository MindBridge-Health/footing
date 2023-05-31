package com.palaver.service.mapper

import com.palaver.data.entity.ScheduledInterviewEntity
import com.palaver.service.model.ScheduledInterview
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.sql.Timestamp
import java.time.Instant

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses=[TimeMapper::class])
interface ScheduledInterviewEntityMapper {

    fun entityToModel(scheduledInterviewEntity: ScheduledInterviewEntity): ScheduledInterview

    fun modelToEntity(scheduledInterview: ScheduledInterview): ScheduledInterviewEntity
}