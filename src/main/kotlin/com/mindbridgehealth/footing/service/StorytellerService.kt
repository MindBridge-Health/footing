package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.data.repository.PreferredTimeRepository
import com.mindbridgehealth.footing.data.repository.StorytellerRepository
import com.mindbridgehealth.footing.service.mapper.PreferredTimeMapper
import com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
@Transactional
class StorytellerService(
    private val db: StorytellerRepository,
    private val preferredTimeRepository: PreferredTimeRepository,
    private val storytellerMapper: StorytellerEntityMapper,
    private val preferredTimeMapper: PreferredTimeMapper,
    private val userDb: MindBridgeUserRepository,
    private val organizationService: OrganizationService
) {


    fun findStorytellerByAltId(altId: String): Optional<Storyteller> {
        val optStoryteller = db.findByAltId(altId)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun findStorytellerEntityByAltId(altId: String): Optional<StorytellerEntity> {
        val optStoryteller = db.findByAltId(altId)
        if(optStoryteller.isPresent) {
            return Optional.of(optStoryteller.get())
        }
        return Optional.empty()
    }

    fun findStorytellerEntityById(id: Int): Optional<StorytellerEntity> {
        val optStoryteller = db.findById(id)
        if(optStoryteller.isPresent) {
            return Optional.of(optStoryteller.get())
        }
        return Optional.empty()
    }

    fun getAllStorytellers(): List<Storyteller> {
        return db.findAll().map { storytellerMapper.entityToModel(it) }
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
            storytellerEntity.isActive = true
            val savedEntity = db.save(storytellerEntity)
            savePreferredTimeEntities(savedEntity)
            savedEntity
        }
    }

    fun update(storyteller: Storyteller, altId: String): Storyteller {
        return storytellerMapper.entityToModel(updateEntity(storyteller, altId))
    }

    fun updateEntity(storyteller: Storyteller, altId: String): StorytellerEntity {
        val storedEntity = db.findByAltId(altId).getOrElse { throw Exception("Unable to find Storyteller to update") }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)
        storedEntity.firstname = storytellerEntity.firstname
        storedEntity.lastname = storytellerEntity.lastname
        storedEntity.middlename = storytellerEntity.middlename
        storedEntity.mobile = storytellerEntity.mobile
        storedEntity.contactMethod = storytellerEntity.contactMethod
        storedEntity.preferredChronicler = storytellerEntity.preferredChronicler
        storedEntity.benefactors = storytellerEntity.benefactors
        storedEntity.email = storytellerEntity.email
        storedEntity.organization = storyteller.organization?.id?.let { organizationService.findEntityByAltId(it) }
        storedEntity.preferredTimes = storytellerEntity.preferredTimes
        storedEntity.isActive = storytellerEntity.isActive

        savePreferredTimeEntities(storedEntity)
        val updatedStorytellerEntity = db.save(storedEntity)
        return updatedStorytellerEntity
    }

    //TODO: Footing-4 Move this behind a PreferredTime Service
    private fun savePreferredTimeEntities(
        storytellerEntity: StorytellerEntity
    ) {
        val dbPfes = preferredTimeRepository.findAllByStoryteller(storytellerEntity)

        //Delete times in db not found in update
        val incomingPfes = storytellerEntity.preferredTimes ?: emptyList()
        val deletedPfes = dbPfes.filter { !incomingPfes.contains(it) }
        preferredTimeRepository.deleteAll(deletedPfes)

        //Add times not found in db
        val newPfes = incomingPfes.filter { !dbPfes.contains(it) }
        newPfes.forEach {
            it.storyteller = storytellerEntity
            preferredTimeRepository.save(it)
        }
    }

    fun deactivateStoryteller(altId: String) {
        val storyteller = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        storyteller.isActive = false
        db.save(storyteller)
    }

    fun hardDeleteStoryteller(altId: String) {
        val storytellerEntity = db.findByAltId(altId).getOrElse { throw Exception("Storyteller not found") }
        db.delete(storytellerEntity)
    }

}