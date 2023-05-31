package com.palaver.service.mapper

import com.palaver.data.entity.BenefactorEntity
import com.palaver.service.model.Benefactor
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.UUID
import kotlin.test.assertEquals

class BenefactorEntityMapperTests {

    @Test
    fun benefactorEntityToBenefactor_validData_validModel() {
        val benefactorEntity = BenefactorEntity()
        benefactorEntity.id = UUID.randomUUID()
        benefactorEntity.lastname = "someBenefactor"
        benefactorEntity.middlename = "middle"
        benefactorEntity.firstname = "first"

        val benefactor = Mappers.getMapper(BenefactorEntityMapper::class.java).entityToModel(benefactorEntity)
        assertEquals(benefactorEntity.id, benefactor.id)
        assertEquals(benefactorEntity.lastname, benefactor.lastname)
        assertEquals(benefactorEntity.middlename, benefactor.middlename)
        assertEquals(benefactorEntity.firstname, benefactor.firstname)
        assertEquals(Benefactor::class.java, benefactor.javaClass)
    }

    @Test
    fun benefactorToBenefactorEntity_validModel_validData() {
        val benefactor = Benefactor(UUID.randomUUID(), "someOtherBenefactor", "first", "middle")

        val benefactorEntity = Mappers.getMapper(BenefactorEntityMapper::class.java).modelToEntity(benefactor)
        assertEquals(benefactor.id, benefactorEntity.id)
        assertEquals(benefactor.lastname, benefactorEntity.lastname)
        assertEquals(benefactorEntity.javaClass, benefactorEntity::class.java)
    }
}