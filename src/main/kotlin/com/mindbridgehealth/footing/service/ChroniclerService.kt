package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.ChroniclerRepository
import com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapper
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class ChroniclerService(private val db: ChroniclerRepository, private val chroniclerEntityMapper: ChroniclerEntityMapper) {
    
    fun findChroniclerById(id: String): Optional<Chronicler> {
//        val optionalChroniclerEntity = db.findByAltId(id)
//        if(optionalChroniclerEntity.isPresent){
//            return Optional.of(chroniclerEntityMapper.entityToModel(optionalChroniclerEntity.get()))
//        }
        return Optional.empty()
    }

    fun save(chronicler: Chronicler): String {
        chronicler.id = null
        val id = db.save(chroniclerEntityMapper.modelToEntity(chronicler)).id
            ?: throw Exception("Unable to save chronicler; DB returned null")
        return Base36Encoder.encode(id.toString())
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