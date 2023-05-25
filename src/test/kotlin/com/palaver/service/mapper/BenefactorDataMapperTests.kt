package com.palaver.service.mapper

import com.palaver.data.generated.BenefactorData
import com.palaver.service.model.Benefactor
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.UUID
import kotlin.test.assertEquals

class BenefactorDataMapperTests {

    @Test
    fun benefactorDataToBenefactor_validData_validModel() {
        val benefactorData = BenefactorData()
        benefactorData.id = UUID.randomUUID()
        benefactorData.name = "someBenefactor"

        val benefactor = Mappers.getMapper(BenefactorDataMapper::class.java).benefactorDataToBenefactor(benefactorData)
        assertEquals(benefactorData.id, benefactor.id)
        assertEquals(benefactorData.name, benefactor.name)
        assertEquals(Benefactor::class.java, benefactor.javaClass)
    }

    @Test
    fun benefactorToBenefactorData_validModel_validData() {
        val benefactor = Benefactor(UUID.randomUUID(), "someOtherBenefactor")

        val benefactorData = Mappers.getMapper(BenefactorDataMapper::class.java).benefactorToBenefactorData(benefactor)
        assertEquals(benefactor.id, benefactorData.id)
        assertEquals(benefactor.name, benefactorData.name)
        assertEquals(benefactorData.javaClass, benefactorData::class.java)
    }
}