package com.palaver.service.mapper

import com.palaver.data.entity.ChroniclerEntity
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*
import kotlin.test.assertEquals

class ChroniclerEntityMapperTests {

    @Test
    fun chroniclerEntityToChronicler_validData_validData() {
        val chroniclerEntity = ChroniclerEntity()
        val uuid = UUID.randomUUID()
        chroniclerEntity.id = uuid
        chroniclerEntity.lastname = "d"
        chroniclerEntity.middlename = "a"
        chroniclerEntity.firstname = "c"
        chroniclerEntity.ai = true

        val chronicler = Mappers.getMapper(ChroniclerEntityMapper::class.java).entityToModel(chroniclerEntity)
        assertEquals(uuid, chronicler.id)
        assertEquals("d", chronicler.lastname)
        assertEquals("a", chronicler.middlename)
        assertEquals("c", chronicler.firstname)
        assertEquals(true, chronicler.isAi)
    }
}