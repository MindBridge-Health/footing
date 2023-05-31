package com.palaver.service.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import java.sql.Timestamp
import java.time.Instant

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
open class TimeMapper {

    fun map(instant: Instant?): Timestamp? {
        return if (instant == null) null else Timestamp.from(instant)
    }

    fun map(timestamp: Timestamp?): Instant? {
        return timestamp?.toInstant()
    }
}