package com.mindbridgehealth.footing.api.dto.mapper

import com.mindbridgehealth.footing.api.dto.StorytellerCreateDto
import com.mindbridgehealth.footing.api.dto.StorytellerListEntry
import com.mindbridgehealth.footing.service.model.Organization
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.mapstruct.*

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class StorytellerListEntryDtoMapper {

    abstract fun storytellerToStorytellerListEntry(storyteller: Storyteller): StorytellerListEntry

}