package com.palaver.service.mapper

import com.palaver.data.generated.BenefactorData
import com.palaver.data.generated.StorytellerData
import com.palaver.service.model.Benefactor
import com.palaver.service.model.Storyteller
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*
import kotlin.test.DefaultAsserter.assertNotNull

class StorytellerDataMapperTests {

    @Test
    fun storytellerDataToStoryteller_validData_validModel() {
        val storytellerData = StorytellerData()
        storytellerData.id = UUID.randomUUID()
        storytellerData.name = "someName"
        storytellerData.contactMethod = "text"

        val benefactorData = BenefactorData()
        benefactorData.id = UUID.randomUUID()
        benefactorData.name = "someBenefactor1"
        val benefactorData2 = BenefactorData()
        benefactorData2.id = UUID.randomUUID()
        benefactorData2.name = "someBenefactor2"

        storytellerData.benefactors = mutableListOf( benefactorData, benefactorData2 )

        val storyteller =  Mappers.getMapper(StorytellerDataMapper::class.java).storytellerDataToStoryteller(storytellerData)

        assertEquals(storytellerData.id, storyteller.id)
        assertEquals(storytellerData.name, storyteller.name)
        assertEquals(storytellerData.contactMethod, storyteller.contactMethod)
        assertNotNull("Benefactors should not be null", storyteller.benefactors)
        assertEquals(2, storyteller.benefactors!!.size)
    }

    @Test
    fun storytellerToStorytellerData_validModel_validData() {
        val benefactor = Benefactor(UUID.randomUUID(), "someBenefactor1")
        val benefactor2 = Benefactor(UUID.randomUUID(), "someBenefactor2")
        val storyteller = Storyteller(UUID.randomUUID(),  "someName","text", mutableListOf(benefactor, benefactor2), null)

        val storytellerData = Mappers.getMapper(StorytellerDataMapper::class.java).storytellerToStorytellerData(storyteller)

        assertEquals(storyteller.id, storytellerData.id)
        assertEquals(storyteller.name, storytellerData.name)
        assertEquals(storyteller.contactMethod, storytellerData.contactMethod)
        assertNotNull("Benefactors should not be null", storytellerData.benefactors)
        assertEquals(2, storytellerData.benefactors!!.size)
    }
}