package com.palaver.service.mapper

import com.palaver.data.entity.ScheduledInterviewEntity
import com.palaver.service.model.ScheduledInterview
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses=[TimeMapper::class, StorytellerEntityMapper::class, ChroniclerEntityMapper::class])
abstract class ScheduledInterviewEntityMapper: IdMapper() {

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun entityToModel(scheduledInterviewEntity: ScheduledInterviewEntity): ScheduledInterview
    @Mapping(source="id", target = "id", ignore = true)
    abstract fun modelToEntity(scheduledInterview: ScheduledInterview): ScheduledInterviewEntity
}