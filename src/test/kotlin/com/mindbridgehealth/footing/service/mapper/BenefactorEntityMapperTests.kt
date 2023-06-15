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
        benefactorEntity.altId = "auth0|648a23ab6ee6f0aa87941142"
        benefactorEntity.lastname = "someBenefactor"
        benefactorEntity.middlename = "middle"
        benefactorEntity.firstname = "first"

        val benefactor = com.mindbridgehealth.footing.service.mapper.BenefactorEntityMapperImpl().entityToModel(benefactorEntity)
        assertEquals("8ub5lac5.648a23ab6ee6f0aa87941142", benefactor.id)
        assertEquals(benefactorEntity.lastname, benefactor.lastname)
        assertEquals(benefactorEntity.middlename, benefactor.middlename)
        assertEquals(benefactorEntity.firstname, benefactor.firstname)
        assertEquals(Benefactor::class.java, benefactor.javaClass)
    }

    @Test
    fun benefactorToBenefactorEntity_validModel_validData() {
        val benefactor = Benefactor( "8ub5lac5.648a23ab6ee6f0aa87941142", "someOtherBenefactor", "first", "middle", "", "")

        val benefactorEntity = com.mindbridgehealth.footing.service.mapper.BenefactorEntityMapperImpl().modelToEntity(benefactor)
        assertEquals("auth0|648a23ab6ee6f0aa87941142", benefactorEntity.altId?.toString())
        assertEquals(benefactor.lastname, benefactorEntity.lastname)
        assertEquals(benefactorEntity.javaClass, benefactorEntity::class.java)
    }
}