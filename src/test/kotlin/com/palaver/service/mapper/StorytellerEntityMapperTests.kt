package com.palaver.service.mapper

import com.palaver.data.entity.BenefactorEntity
import com.palaver.data.entity.StorytellerEntity
import com.palaver.service.model.Benefactor
import com.palaver.service.model.Storyteller
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*
import kotlin.test.DefaultAsserter.assertNotNull

class StorytellerEntityMapperTests {

    @Test
    fun storytellerDataToStoryteller_validData_validModel() {
        val storytellerEntity = StorytellerEntity()
        storytellerEntity.id = UUID.randomUUID()
        storytellerEntity.lastname = "someName"
        storytellerEntity.middlename = "middle"
        storytellerEntity.firstname = "first"
        storytellerEntity.contactMethod = "text"

        val benefactorEntity = BenefactorEntity()
        benefactorEntity.id = UUID.randomUUID()
        benefactorEntity.lastname = "someBenefactor1"
        benefactorEntity.middlename = "middle"
        benefactorEntity.firstname = "first"
        val benefactorEntity2 = BenefactorEntity()
        benefactorEntity2.id = UUID.randomUUID()
        benefactorEntity2.lastname = "someBenefactor2"
        benefactorEntity2.middlename = "middle"
        benefactorEntity2.firstname = "first"

        storytellerEntity.benefactors = mutableListOf( benefactorEntity, benefactorEntity2 )

        val storyteller =  Mappers.getMapper(StorytellerEntityMapper::class.java).entityToModel(storytellerEntity)

        assertEquals(storytellerEntity.id, storyteller.id)
        assertEquals(storytellerEntity.lastname, storyteller.lastname)
        assertEquals(storytellerEntity.contactMethod, storyteller.contactMethod)
        assertNotNull("Benefactors should not be null", storyteller.benefactors)
        assertEquals(2, storyteller.benefactors!!.size)
    }

    @Test
    fun storytellerToStorytellerData_validModel_validData() {
        val benefactor = Benefactor(UUID.randomUUID(), "someBenefactor1", "first", "middle")
        val benefactor2 = Benefactor(UUID.randomUUID(), "someBenefactor2", "first", "middle")
        val storyteller = Storyteller(UUID.randomUUID(),  "someName", "first", "middle","text", mutableListOf(benefactor, benefactor2), null)

        val storytellerData = Mappers.getMapper(StorytellerEntityMapper::class.java).modelToEntity(storyteller)

        assertEquals(storyteller.id, storytellerData.id)
        assertEquals(storyteller.lastname, storytellerData.lastname)
        assertEquals(storyteller.contactMethod, storytellerData.contactMethod)
        assertNotNull("Benefactors should not be null", storytellerData.benefactors)
        assertEquals(2, storytellerData.benefactors!!.size)
    }
}