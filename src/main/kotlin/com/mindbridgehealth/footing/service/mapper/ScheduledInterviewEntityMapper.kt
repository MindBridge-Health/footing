package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.ScheduledInterviewEntity
import com.mindbridgehealth.footing.service.model.ScheduledInterview
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses=[TimeMapper::class, StorytellerEntityMapper::class, ChroniclerEntityMapper::class, InterviewEntityMapper::class])
abstract class ScheduledInterviewEntityMapper: IdMapper() {

    @Mapping(source="id", target = "id", ignore = true)
    abstract fun entityToModel(scheduledInterviewEntity: ScheduledInterviewEntity): ScheduledInterview
    @Mapping(source="id", target = "id", ignore = true)
    abstract fun modelToEntity(scheduledInterview: ScheduledInterview): ScheduledInterviewEntity
}