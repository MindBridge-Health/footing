package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.data.repository.PreferredTimeRepository
import com.mindbridgehealth.footing.data.repository.StorytellerRepository
import com.mindbridgehealth.footing.service.mapper.PreferredTimeMapper
import com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class StorytellerService(
    private val db: StorytellerRepository,
    private val preferredTimeRepository: PreferredTimeRepository,
    private val storytellerMapper: StorytellerEntityMapper,
    private val preferredTimeMapper: PreferredTimeMapper,
    private val userDb: MindBridgeUserRepository
) {

    fun findStorytellerByAltId(altId: String): Optional<Storyteller> {
        val optStoryteller = db.findByAltIdAndIsActive(altId, true)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun findStorytellerEntityByAltId(altId: String): Optional<StorytellerEntity> {
        val optStoryteller = db.findByAltIdAndIsActive(altId, true)
        if(optStoryteller.isPresent) {
            return Optional.of(optStoryteller.get())
        }
        return Optional.empty()
    }

    fun findStorytellerEntityById(id: Int): Optional<StorytellerEntity> {
        val optStoryteller = db.findByIdAndIsActive(id, true)
        if(optStoryteller.isPresent) {
            return Optional.of(optStoryteller.get())
        }
        return Optional.empty()
    }

    fun save(storyteller: Storyteller, altId: String): Storyteller {
        return storytellerMapper.entityToModel(saveEntity(storyteller, altId))
    }

    fun saveEntity(storyteller: Storyteller, altId: String): StorytellerEntity {

        val userEntityOptional = userDb.findByAltId(altId)
        return if(userEntityOptional.isPresent) {
            throw Exception("User already exists")
        } else {
            val storytellerEntity = storytellerMapper.modelToEntity(storyteller.copy(id = null))
            storytellerEntity.altId = altId
            val savedEntity = db.save(storytellerEntity)
            savePreferredTimeEntities(storyteller, savedEntity)
            savedEntity
        }
    }

    fun update(storyteller: Storyteller, altId: String): Storyteller {
        return storytellerMapper.entityToModel(updateEntity(storyteller, altId))
    }

    fun updateEntity(storyteller: Storyteller, altId: String): StorytellerEntity {
        val storedEntity = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception("Unable to find Storyteller to update") }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)
        storedEntity.firstname = storytellerEntity.firstname
        storedEntity.lastname = storytellerEntity.lastname
        storedEntity.middlename = storytellerEntity.middlename
        storedEntity.mobile = storytellerEntity.mobile
        storedEntity.contactMethod = storytellerEntity.contactMethod
        storedEntity.preferredChronicler = storytellerEntity.preferredChronicler
        storedEntity.benefactors = storytellerEntity.benefactors
        storedEntity.email = storytellerEntity.email

        val pfes = savePreferredTimeEntities(storyteller, storedEntity)
        val updatedStorytellerEntity = db.save(storedEntity)
        updatedStorytellerEntity.preferredTimes = pfes
        return updatedStorytellerEntity
    }

    //TODO: Footing-4 Move this behind a PreferredTime Service
    private fun savePreferredTimeEntities(
        storyteller: Storyteller,
        storytellerEntity: StorytellerEntity
    ): ArrayList<PreferredTimeEntity> {
        val pfes = ArrayList<PreferredTimeEntity>()
        storyteller.preferredTimes?.let {
            preferredTimes -> preferredTimes.forEach {
                val existingRecord = preferredTimeRepository.findByStorytellerAndDayOfWeekAndTime(
                    storytellerEntity,
                    it.dayOfWeek.name,
                    it.time
                )
                if (existingRecord.isEmpty) {
                    val preferredTimeEntity = preferredTimeMapper.modelToEntity(it)
                    preferredTimeEntity.storyteller = storytellerEntity
                    pfes.add(preferredTimeRepository.save(preferredTimeEntity))
                }
            }
        }
        return pfes
    }

    fun deactivateStoryteller(altId: String) {
        val storyteller = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        storyteller.isActive = false
        db.save(storyteller)
    }

}