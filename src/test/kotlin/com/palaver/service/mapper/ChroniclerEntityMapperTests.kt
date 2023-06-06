package com.palaver.service.mapper

import com.palaver.data.entity.ChroniclerEntity
import com.palaver.service.util.Base36Encoder
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
        chroniclerEntity.lastname = "d"
        chroniclerEntity.middlename = "a"
        chroniclerEntity.firstname = "c"
        chroniclerEntity.ai = true

        val chronicler = Mappers.getMapper(ChroniclerEntityMapper::class.java).entityToModel(chroniclerEntity)
        assertEquals(id, Base36Encoder.decode(chronicler.id!!).toInt())
        assertEquals("d", chronicler.lastname)
        assertEquals("a", chronicler.middlename)
        assertEquals("c", chronicler.firstname)
        assertEquals(true, chronicler.isAi)
    }
}