package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.PreferredTimeRepository
import com.mindbridgehealth.footing.data.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.mapper.PreferredTimeMapper
import com.mindbridgehealth.footing.service.model.Storyteller as ServiceStoryteller
import com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapper
import com.mindbridgehealth.footing.service.model.PreferredTime
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.optionals.getOrElse


@Service
class StorytellerService(private val db : com.mindbridgehealth.footing.data.StorytellerRepository, private val preferredTimeRepository: PreferredTimeRepository, private val storytellerMapper: StorytellerEntityMapper,
    private val chroniclerService: ChroniclerService, private val chroniclerMapper: ChroniclerEntityMapper, private val preferredTimeMapper: PreferredTimeMapper) {

    fun findStorytellerById(id: String): Optional<ServiceStoryteller> {
        val optStoryteller = db.findByIdAndIsActive(Base36Encoder.decode(id).toInt(), true)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun save(storyteller: ServiceStoryteller): String {
        if(storyteller.preferredTimes != null) {
            storyteller.preferredTimes!!.forEach { preferredTimeRepository.save(preferredTimeMapper.modelToEntity(it)) }
        }
        return storytellerMapper.entityToModel(db.save(storytellerMapper.modelToEntity(storyteller.copy(id = null)))).id ?: throw Exception()
    }

    fun update(storyteller: ServiceStoryteller): Storyteller {
        if(storyteller.id == null) {
            throw Exception()
        }
        val storedEntity = db.findByIdAndIsActive(Base36Encoder.decode(storyteller.id!!).toInt(), true).getOrElse { throw Exception() }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)

        //Todo: Gotta fill the preferredTime storytellerId and map it
        val pfes = ArrayList<PreferredTimeEntity>()
        if(storyteller.preferredTimes != null) {
            storyteller.preferredTimes!!.forEach {
                val existingRecord = preferredTimeRepository.findByStorytellerAndDayOfWeekAndTime(storytellerEntity, it.dayOfWeek.name, it.time)
                if(existingRecord.isEmpty) {
                    val preferredTimeEntity = preferredTimeMapper.modelToEntity(it)
                    preferredTimeEntity.storyteller = storytellerEntity
                    preferredTimeRepository.save(preferredTimeEntity)
                    pfes.add(preferredTimeEntity)
                }
            }
        }

        storytellerEntity.version = storedEntity.version
        storytellerEntity.preferredTimes = pfes
        return storytellerMapper.entityToModel(db.save(storytellerEntity))
    }

    fun deactivateStoryteller(id: String) {
        val storyteller = db.findByIdAndIsActive(Base36Encoder.decode(id).toInt(), true).getOrElse { throw Exception() }
        storyteller.isActive = false
        db.save(storyteller)
    }

}