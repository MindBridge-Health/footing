package com.palaver.service

import com.palaver.data.generated.StorytellerData
import com.palaver.service.model.Storyteller as ServiceStoryteller
import com.palaver.data.StorytellerRepository
import com.palaver.service.mapper.StorytellerDataMapper
import com.palaver.service.model.Storyteller
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.util.*

@Service
class StorytellerService(val db : com.palaver.data.StorytellerRepository) {
    fun findStorytellers(): List<ServiceStoryteller> = db.findAll().toList().map {
        dbStoryteller -> Mappers.getMapper(StorytellerDataMapper::class.java).storytellerDataToStoryteller(dbStoryteller)
    }

    fun findStorytellerById(id: UUID): List<ServiceStoryteller> = db.findById(id).toList().map {
        dbStoryteller -> Mappers.getMapper(StorytellerDataMapper::class.java).storytellerDataToStoryteller(dbStoryteller)
    }

    fun save(storyteller: ServiceStoryteller) {
        storyteller.id = null
//        if (storyteller.contactMethod == null) {
//            storyteller.contactMethod = ""
//        }
        db.save(Mappers.getMapper(StorytellerDataMapper::class.java).storytellerToStorytellerData(storyteller))
    }

    fun update(storyteller: ServiceStoryteller) {
        db.save(Mappers.getMapper(StorytellerDataMapper::class.java).storytellerToStorytellerData(storyteller))
    }



    fun <T : Any> Optional<out T>.toList(): List<T> =
            if (isPresent) listOf(get()) else emptyList()

    //TODO: While handy for early testing, make sure this never sees anything close to production!
    fun deleteAll() {
        db.deleteAll()
    }
}