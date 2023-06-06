package com.palaver.service

import com.palaver.service.mapper.ChroniclerEntityMapper
import com.palaver.service.model.Storyteller as ServiceStoryteller
import com.palaver.service.mapper.StorytellerEntityMapper
import com.palaver.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class StorytellerService(private val db : com.palaver.data.StorytellerRepository, private val storytellerMapper: StorytellerEntityMapper,
    private val chroniclerService: ChroniclerService, private val chroniclerMapper: ChroniclerEntityMapper) {

    fun findStorytellerById(id: String): Optional<ServiceStoryteller> {
        val optStoryteller = db.findByIdAndIsActive(Base36Encoder.decode(id).toInt(), true)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun save(storyteller: ServiceStoryteller): String {
        return storytellerMapper.entityToModel(db.save(storytellerMapper.modelToEntity(storyteller.copy(id = null)))).id ?: throw Exception()
    }

    fun update(storyteller: ServiceStoryteller) {
        if(storyteller.id == null) {
            throw Exception()
        }
        val storedEntity = db.findByIdAndIsActive(Base36Encoder.decode(storyteller.id!!).toInt(), true).getOrElse { throw Exception() }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)
        storytellerEntity.version = storedEntity.version
        db.save(storytellerEntity)
    }

    fun deactivateStoryteller(id: String) {
        val storyteller = db.findByIdAndIsActive(Base36Encoder.decode(id).toInt(), true).getOrElse { throw Exception() }
        storyteller.isActive = false
        db.save(storyteller)
    }

}