package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.PreferredTimeRepository
import com.mindbridgehealth.footing.data.entity.PreferredTimeEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.data.repository.StorytellerRepository
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.mapper.PreferredTimeMapper
import com.mindbridgehealth.footing.service.model.Storyteller as ServiceStoryteller
import com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapper
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.optionals.getOrElse


@Service
class StorytellerService(private val db : StorytellerRepository, private val preferredTimeRepository: PreferredTimeRepository, private val storytellerMapper: StorytellerEntityMapper,
                         private val chroniclerService: ChroniclerService, private val chroniclerMapper: ChroniclerEntityMapper, private val preferredTimeMapper: PreferredTimeMapper,
    private val userDb: MindBridgeUserRepository, private val em: EntityManager) {

    fun findStorytellerById(id: String): Optional<ServiceStoryteller> {
        val optStoryteller = db.findByAltIdAndIsActive(Base36Encoder.decodeAltId(id), true)
        if(optStoryteller.isPresent) {
            return Optional.of(storytellerMapper.entityToModel(optStoryteller.get()))
        }
        return Optional.empty()
    }

    fun save(storyteller: ServiceStoryteller): String {
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller.copy(id = null))
        val storytellerEntity1 = db.save(storytellerEntity)
        savePreferredTimeEntities(storyteller, storytellerEntity1)
        return storytellerMapper.entityToModel(storytellerEntity1).id ?: throw Exception()
    }

    fun update(storyteller: ServiceStoryteller, altId: String): Storyteller {
        val storedEntity = userDb.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        val storytellerEntity = storytellerMapper.modelToEntity(storyteller)

        storytellerEntity.id = storedEntity.id
        storytellerEntity.altId = storedEntity.altId
        storytellerEntity.version = storedEntity.version
        storytellerEntity.email = storedEntity.email

        storytellerEntity.firstname = storyteller.firstname
        storytellerEntity.lastname = storyteller.lastname
        storytellerEntity.middlename = storyteller.middlename
        storytellerEntity.mobile = storyteller.mobile
        storytellerEntity.contactMethod = storyteller.contactMethod

        val pfes = savePreferredTimeEntities(storyteller, storytellerEntity)

        storytellerEntity.preferredTimes = pfes
        db.mergeWithUser(storytellerEntity.id!!, null, storytellerEntity.contactMethod, storytellerEntity.onboardingStatus)

        return storytellerMapper.entityToModel(storytellerEntity)
    }

    //TODO Move this behind a PreferredTime Service
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

    fun deactivateStoryteller(id: String) {
        val storyteller = db.findByIdAndIsActive(Base36Encoder.decode(id).toInt(), true).getOrElse { throw Exception() }
        storyteller.isActive = false
        db.save(storyteller)
    }

}