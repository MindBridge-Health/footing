package com.palaver.service.mapper

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
 class TimeMapper {

    fun map(instant: Instant?): Timestamp? {
        return if (instant == null) null else Timestamp.from(instant)
    }

    fun map(timestamp: Timestamp?): Instant? {
        return timestamp?.toInstant()
    }
}