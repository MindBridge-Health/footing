package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.service.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.data.repository.PreferredTimeRepository
import com.mindbridgehealth.footing.data.repository.StorytellerRepository
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.mapper.PreferredTimeMapper
import com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class StorytellerService(private val db : StorytellerRepository, private val preferredTimeRepository: PreferredTimeRepository, private val storytellerMapper: StorytellerEntityMapper,
                         private val chroniclerService: ChroniclerService, private val chroniclerMapper: ChroniclerEntityMapper, private val preferredTimeMapper: PreferredTimeMapper,
                         private val userDb: MindBridgeUserRepository
) {

    fun findStorytellerById(id: String): Optional<Storyteller> {
        val optStoryteller = db.findByAltIdAndIsActive(Base36Encoder.decodeAltId(id), true)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun findStorytellerEntityByAltId(id: String): Optional<StorytellerEntity> {
        val optStoryteller = db.findByAltIdAndIsActive(Base36Encoder.decodeAltId(id), true)
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
        if(userDb.findByAltId(altId).isPresent) {
            throw Exception("User Already Exists")
        }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller.copy(id = null))
        storytellerEntity.altId = altId
        val savedEntity = db.save(storytellerEntity)
        savePreferredTimeEntities(storyteller, savedEntity)
        return savedEntity
    }

    fun update(storyteller: Storyteller, altId: String): Storyteller {
        return storytellerMapper.entityToModel(updateEntity(storyteller, altId))
    }

    fun updateEntity(storyteller: Storyteller, altId: String): StorytellerEntity {
        val storedEntity = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)

        storytellerEntity.id = storedEntity.id
        storytellerEntity.altId = storedEntity.altId
        storytellerEntity.version = storedEntity.version
        storytellerEntity.email = storedEntity.email
        storytellerEntity.contactMethod = storyteller.contactMethod

        storedEntity.firstname = storyteller.firstname
        storedEntity.lastname = storyteller.lastname
        storedEntity.middlename = storyteller.middlename
        storedEntity.mobile = storyteller.mobile


        val pfes = savePreferredTimeEntities(storyteller, storytellerEntity)

        storytellerEntity.preferredTimes = pfes
        db.save(storedEntity)

        return storytellerEntity
    }

    //TODO: Footing-4 Move this behind a PreferredTime Service
    private fun savePreferredTimeEntities(
        storyteller: Storyteller,
        storytellerEntity: StorytellerEntity
    ): ArrayList<PreferredTimeEntity> {
        val pfes = ArrayList<PreferredTimeEntity>()
        if (storyteller.preferredTimes != null) {
            storyteller.preferredTimes!!.forEach {
                val existingRecord = preferredTimeRepository.findByStorytellerAndDayOfWeekAndTime(
                    storytellerEntity,
                    it.dayOfWeek.name,
                    it.time
                )
                if (existingRecord.isEmpty) {
                    val preferredTimeEntity = preferredTimeMapper.modelToEntity(it)
                    preferredTimeEntity.storyteller = storytellerEntity
                    preferredTimeRepository.save(preferredTimeEntity)
                    pfes.add(preferredTimeEntity)
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