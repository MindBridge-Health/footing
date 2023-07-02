package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.service.entity.ChroniclerEntity
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*
import kotlin.math.floor
import kotlin.test.assertEquals

class ChroniclerEntityMapperTests {

    @Test
    fun chroniclerEntityToChronicler_validData_validData() {
        val chroniclerEntity = ChroniclerEntity()
        val id =  floor(Math.random() * 1000).toInt()
        chroniclerEntity.id = id
        chroniclerEntity.altId = "auth0|648a23ab6ee6f0aa87941142"
        chroniclerEntity.lastname = "d"
        chroniclerEntity.middlename = "a"
        chroniclerEntity.firstname = "c"
        chroniclerEntity.ai = true

        val chronicler = Mappers.getMapper(ChroniclerEntityMapper::class.java).entityToModel(chroniclerEntity)
        assertEquals("8ub5lac5.648a23ab6ee6f0aa87941142", chronicler.id)
        assertEquals("d", chronicler.lastname)
        assertEquals("a", chronicler.middlename)
        assertEquals("c", chronicler.firstname)
        assertEquals(true, chronicler.isAi)
    }
}