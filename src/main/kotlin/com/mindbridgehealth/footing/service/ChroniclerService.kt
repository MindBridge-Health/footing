package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.ChroniclerRepository
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.model.Chronicler
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class ChroniclerService(private val db: ChroniclerRepository, private val chroniclerEntityMapper: ChroniclerEntityMapper, private val userDb: MindBridgeUserRepository, private val organizationService: OrganizationService) {
    
    fun findChroniclerByAltId(id: String): Optional<Chronicler> {
        val optionalChroniclerEntity = db.findByAltIdAndIsActive(id, true)
        if(optionalChroniclerEntity.isPresent){
            return Optional.of(chroniclerEntityMapper.entityToModel(optionalChroniclerEntity.get()))
        }
        return Optional.empty()
    }

    fun findChroniclerEntityByAltId(id: String): Optional<ChroniclerEntity> {
        return db.findByAltIdAndIsActive(id, true)
    }

    fun save(chronicler: Chronicler, altId: String): Chronicler {
        return chroniclerEntityMapper.entityToModel(saveEntity(chronicler, altId))
    }
    fun saveEntity(chronicler: Chronicler, altId: String): ChroniclerEntity {
        if(userDb.findByAltId(altId).isPresent) {
            throw Exception("User Already Exists")
        }
        val chroniclerEntity = chroniclerEntityMapper.modelToEntity(chronicler.copy(id = null))
        chroniclerEntity.altId = altId
        val savedEntity = db.save(chroniclerEntity)
        return savedEntity
    }

    fun update(chronicler: Chronicler, altId: String): Chronicler {
        val storedEntity = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }

        storedEntity.firstname = chronicler.firstname
        storedEntity.lastname = chronicler.lastname
        storedEntity.middlename = chronicler.middlename
        storedEntity.mobile = chronicler.mobile
        storedEntity.ai = chronicler.isAi
        storedEntity.organization = chronicler.organization?.id?.let { organizationService.findEntityByAltId(it) }

        return chroniclerEntityMapper.entityToModel(db.save(storedEntity))
    }

    fun deactivateChronicler(id: String) {
        val chronicler = db.findById(id.toInt()).getOrElse { throw Exception() } //TODO Fix altId
        chronicler.isActive = false
        db.save(chronicler)
    }
}