package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.BenefactorEntity
import com.mindbridgehealth.footing.service.model.Benefactor
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.UUID
import kotlin.math.floor
import kotlin.test.assertEquals

class BenefactorEntityMapperTests {

    @Test
    fun benefactorEntityToBenefactor_validData_validModel() {
        val benefactorEntity = BenefactorEntity()
        benefactorEntity.id =  floor(Math.random() * 1000).toInt()
        benefactorEntity.lastname = "someBenefactor"
        benefactorEntity.middlename = "middle"
        benefactorEntity.firstname = "first"

        val benefactor = com.mindbridgehealth.footing.service.mapper.BenefactorEntityMapperImpl().entityToModel(benefactorEntity)
        assertEquals(benefactorEntity.id, Base36Encoder.decode(benefactor.id!!).toInt())
        assertEquals(benefactorEntity.lastname, benefactor.lastname)
        assertEquals(benefactorEntity.middlename, benefactor.middlename)
        assertEquals(benefactorEntity.firstname, benefactor.firstname)
        assertEquals(Benefactor::class.java, benefactor.javaClass)
    }

    @Test
    fun benefactorToBenefactorEntity_validModel_validData() {
        val benefactor = Benefactor( Base36Encoder.encode(floor(Math.random() * 1000).toInt().toString()), "someOtherBenefactor", "first", "middle", "", "")

        val benefactorEntity = com.mindbridgehealth.footing.service.mapper.BenefactorEntityMapperImpl().modelToEntity(benefactor)
        assertEquals(Base36Encoder.decode(benefactor.id!!), benefactorEntity.id?.toString())
        assertEquals(benefactor.lastname, benefactorEntity.lastname)
        assertEquals(benefactorEntity.javaClass, benefactorEntity::class.java)
    }
}