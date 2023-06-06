package com.palaver.service.mapper

import com.palaver.data.entity.MediaEntity
import com.palaver.service.model.Media
import com.palaver.service.util.Base36Encoder
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

import java.net.URI
import java.util.UUID
import kotlin.math.floor
import kotlin.test.assertEquals

class MediaEntityMapperTests {
    @Test
    fun mediaToMediaEntity_validData_validData() {
        val media = Media(Base36Encoder.encode(floor(Math.random() * 1000).toInt().toString()), "name", null, URI("http://localhost/somewhere"),"MP4", null, null)

        val mem = MediaEntityMapperImpl(StorytellerEntityMapperImpl(BenefactorEntityMapperImpl(), ChroniclerEntityMapperImpl()))
        val mediaEntity = mem.modelToEntity(media)

        assertEquals("http://localhost/somewhere", mediaEntity.location)
    }

    @Test
    fun mediaEntityToMedia_validData_validData() {
        val mediaEntity = MediaEntity()
        with(mediaEntity) {
            id = floor(Math.random() * 1000).toInt()
            name = "name"
            location = "http://localhost/somewhere"
            type = "MP4"
        }

        val mem = MediaEntityMapperImpl(StorytellerEntityMapperImpl(BenefactorEntityMapperImpl(), ChroniclerEntityMapperImpl()))
        val media = mem.entityToModel(mediaEntity)

        assertEquals(URI.create("http://localhost/somewhere"), media.location)
    }
}