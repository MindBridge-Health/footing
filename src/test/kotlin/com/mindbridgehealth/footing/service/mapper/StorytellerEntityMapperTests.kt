package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.BenefactorEntity
import com.mindbridgehealth.footing.data.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.model.Benefactor
import com.mindbridgehealth.footing.service.model.Chronicler
import com.mindbridgehealth.footing.service.model.OnboardingStatus
import com.mindbridgehealth.footing.service.model.Storyteller
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.test.DefaultAsserter.assertNotNull

class StorytellerEntityMapperTests {

    @Test
    fun storytellerDataToStoryteller_validData_validModel() {
        val storytellerEntity = StorytellerEntity()
        storytellerEntity.id = floor(Math.random() * 1000).toInt()
        storytellerEntity.altId = "auth0|648a23ab6ee6f0aa87941142"
        storytellerEntity.lastname = "someName"
        storytellerEntity.middlename = "middle"
        storytellerEntity.firstname = "first"
        storytellerEntity.contactMethod = "text"

        val benefactorEntity = BenefactorEntity()
        benefactorEntity.id = floor(Math.random() * 1000).toInt()
        benefactorEntity.altId = "auth0|648a23ab6ee6f0aa87941142"
        benefactorEntity.lastname = "someBenefactor1"
        benefactorEntity.middlename = "middle"
        benefactorEntity.firstname = "first"
        val benefactorEntity2 = BenefactorEntity()
        benefactorEntity2.id = floor(Math.random() * 1000).toInt()
        benefactorEntity2.altId = "auth0|648a23ab6ee6f0aa87941142"
        benefactorEntity2.lastname = "someBenefactor2"
        benefactorEntity2.middlename = "middle"
        benefactorEntity2.firstname = "first"

        storytellerEntity.benefactors = mutableListOf( benefactorEntity, benefactorEntity2 )

        val storyteller =  StorytellerEntityMapperImpl(
            BenefactorEntityMapperImpl(),
            ChroniclerEntityMapperImpl(),
            PreferredTimeMapperImpl()
        ).entityToModel(storytellerEntity)

        assertEquals(storytellerEntity.altId, Base36Encoder.decodeAltId(storyteller.id!!))
        assertEquals(storytellerEntity.lastname, storyteller.lastname)
        assertEquals(storytellerEntity.contactMethod, storyteller.contactMethod)
        assertNotNull("Benefactors should not be null", storyteller.benefactors)
        assertEquals(2, storyteller.benefactors!!.size)
    }

    @Test
    fun storytellerToStorytellerData_validModel_validData() {
        val benefactor = Benefactor( "8ub5lac5.648a23ab6ee6f0aa87941142", "someBenefactor1", "first", "middle", "", "")
        val benefactor2 = Benefactor( "8ub5lac5.648a23ab6ee6f0aa87941142", "someBenefactor2", "first", "middle", "", "")
        val chronicler = Chronicler( "8ub5lac5.648a23ab6ee6f0aa87941142", "d","a","c","", "", true)

        val storyteller = Storyteller("8ub5lac5.648a23ab6ee6f0aa87941142",  "someName", "first", "middle","", "text", "", mutableListOf(benefactor, benefactor2), chronicler, OnboardingStatus.ONBOARDING_NOT_STARTED, null)

        val mapper = StorytellerEntityMapperImpl(
            BenefactorEntityMapperImpl(),
            ChroniclerEntityMapperImpl(),
            PreferredTimeMapperImpl()
        )
        val storytellerData = mapper.modelToEntity(storyteller)

        assertEquals(Base36Encoder.decodeAltId(storyteller.id!!), storytellerData.altId)
        assertEquals(storyteller.lastname, storytellerData.lastname)
        assertEquals(storyteller.contactMethod, storytellerData.contactMethod)
        assertNotNull("Benefactors should not be null", storytellerData.benefactors)
        assertEquals(2, storytellerData.benefactors!!.size)
        assertEquals(0, storytellerData.onboardingStatus)
    }

}