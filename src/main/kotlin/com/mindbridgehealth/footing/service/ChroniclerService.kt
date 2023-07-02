package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.ChroniclerRepository
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class ChroniclerService(private val db: ChroniclerRepository, private val chroniclerEntityMapper: ChroniclerEntityMapper, private val userDb: MindBridgeUserRepository) {
    
    fun findChroniclerById(id: String): Optional<Chronicler> {
        val optionalChroniclerEntity = db.findByAltIdAndIsActive(Base36Encoder.decodeAltId(id), true)
        if(optionalChroniclerEntity.isPresent){
            return Optional.of(chroniclerEntityMapper.entityToModel(optionalChroniclerEntity.get()))
        }
        return Optional.empty()
    }

    fun findChroniclerEntityById(id: String): Optional<ChroniclerEntity> {
        return db.findByAltIdAndIsActive(Base36Encoder.decodeAltId(id), true)
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

    fun update(chronicler: Chronicler) {
        if(chronicler.id != null) findChroniclerById(chronicler.id!!) else throw Exception() //TODO: Footing-2 Exception
        db.save(chroniclerEntityMapper.modelToEntity(chronicler))
    }

    fun deactivateChronicler(id: String) {
        val chronicler = db.findById(Base36Encoder.decode(id).toInt()).getOrElse { throw Exception() } //TODO Fix altId
        chronicler.isActive = false
        db.save(chronicler)
    }
}