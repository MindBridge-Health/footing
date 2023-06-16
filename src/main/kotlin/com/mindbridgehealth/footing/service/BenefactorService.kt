package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.BenefactorRepository
import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.service.mapper.BenefactorEntityMapper
import com.mindbridgehealth.footing.service.model.Benefactor
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class BenefactorService(private val db : BenefactorRepository, private val benefactorMapper: BenefactorEntityMapper,  private val userDb: MindBridgeUserRepository) {

    fun findBenefactorById(id: String): Optional<Benefactor> {
        val optBenefactor = db.findByAltIdAndIsActive(id, true)
        if(optBenefactor.isPresent) {
            return Optional.of(benefactorMapper.entityToModel(optBenefactor.get()))
        }
        return Optional.empty()
    }

    fun save(benefactor: Benefactor, altId: String): String {
        if(userDb.findByAltId(altId).isPresent) {
            return "User Already Exists"
        }
        val benefactorEntity = benefactorMapper.modelToEntity(benefactor.copy(id = null))
        benefactorEntity.altId = altId
        val savedEntity = db.save(benefactorEntity)
        return benefactorMapper.entityToModel(savedEntity).id ?: throw Exception()
    }

    fun update(benefactor: Benefactor, altId: String): Benefactor {
        val storedEntity = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        val benefactorEntity = benefactorMapper.modelToEntity(benefactor)

        benefactorEntity.id = storedEntity.id
        benefactorEntity.altId = storedEntity.altId
        benefactorEntity.version = storedEntity.version
        benefactorEntity.email = storedEntity.email

        storedEntity.firstname = benefactor.firstname
        storedEntity.lastname = benefactor.lastname
        storedEntity.middlename = benefactor.middlename
        storedEntity.mobile = benefactor.mobile

        db.save(storedEntity)

        return benefactorMapper.entityToModel(benefactorEntity)
    }

    fun deactivateBenefactor(altId: String) {
        val benefactor = db.findByAltIdAndIsActive(altId, true).getOrElse { throw Exception() }
        benefactor.isActive = false
        db.save(benefactor)
    }

}