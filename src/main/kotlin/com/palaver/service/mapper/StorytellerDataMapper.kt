package com.palaver.service.mapper

import com.palaver.data.generated.StorytellerData
import com.palaver.service.model.Storyteller
import org.mapstruct.Mapper

@Mapper
interface StorytellerDataMapper {

    fun storytellerDataToStoryteller(storytellerData: StorytellerData) : Storyteller

    fun storytellerToStorytellerData(storyteller: Storyteller) : StorytellerData
}