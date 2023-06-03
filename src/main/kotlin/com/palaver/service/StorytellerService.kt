package com.palaver.service

import com.palaver.service.mapper.StoryGroupEntityMapper
import com.palaver.service.model.Storyteller as ServiceStoryteller
import com.palaver.service.mapper.StorytellerEntityMapper
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class StorytellerService(val db : com.palaver.data.StorytellerRepository, @Autowired val storytellerMapper: StorytellerEntityMapper) {
    fun findStorytellers(): List<ServiceStoryteller> = db.findAll().toList().map {
        dbStoryteller -> storytellerMapper.entityToModel(dbStoryteller)
    }

    fun findStorytellerById(id: UUID): Optional<ServiceStoryteller> {
        val optStoryteller = db.findById(id)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun save(storyteller: ServiceStoryteller): UUID {
        storyteller.id = null
//        if (storyteller.contactMethod == null) {
//            storyteller.contactMethod = ""
//        }
        val id = db.save(storytellerMapper.modelToEntity(storyteller)).id
            ?: throw Exception("Unable to save storyteller; DB returned null")
        return id
    }

    fun update(storyteller: ServiceStoryteller) {
        db.save(storytellerMapper.modelToEntity(storyteller))
    }

    fun deleteStoryteller(id: UUID) {
        db.deleteById(id)
    }

    fun <T : Any> Optional<out T>.toList(): List<T> =
            if (isPresent) listOf(get()) else emptyList()

    //TODO: While handy for early testing, make sure this never sees anything close to production!
    fun deleteAll() {
        db.deleteAll()
    }
}