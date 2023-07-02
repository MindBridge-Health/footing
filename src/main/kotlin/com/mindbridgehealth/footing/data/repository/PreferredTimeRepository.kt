package com.mindbridgehealth.footing.data.repository

import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.sql.Time
import java.time.DayOfWeek
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface PreferredTimeRepository: JpaRepository<PreferredTimeEntity, JvmType.Object> {

    fun findByStorytellerAndDayOfWeekAndTime(storytellerEntity: StorytellerEntity, dayOfWeek: String, time: Time): Optional<PreferredTimeEntity>
}