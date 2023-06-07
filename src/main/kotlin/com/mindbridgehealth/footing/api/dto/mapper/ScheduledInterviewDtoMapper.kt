package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.ScheduleInterviewResponseDto
import com.mindbridgehealth.footing.service.model.ScheduledInterview
import org.mapstruct.AfterMapping
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class ScheduledInterviewDtoMapper {
    companion object {
        //TODO Convert to user's timezone?
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))

        fun formatInstant(instant: Instant): String {
            return formatter.format(instant)
        }
    }
    @Mapping(target="interviewId", ignore = true)
    @Mapping(target="scheduledTime", ignore = true)
    abstract fun modelToDto(scheduledInterview: ScheduledInterview): ScheduleInterviewResponseDto

    @AfterMapping
    fun populateInterviewId(source: ScheduledInterview, @MappingTarget target: ScheduleInterviewResponseDto) {
        target.interviewId = source.interview.id
        target.scheduledTime = formatInstant(source.scheduledTime!!)
    }

}