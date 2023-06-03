package com.palaver.service.mapper

import com.palaver.data.entity.MediaEntity
import com.palaver.service.model.Media
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.net.URI
import java.util.UUID
import kotlin.test.assertEquals

class MediaEntityMapperTests {

    @Test
    fun mediaToMediaEntity_validData_validData() {
        val media = Media(UUID.randomUUID(), "name", null, URI("http://localhost/somewhere"),"MP4", null, null)

        val mediaEntity = Mappers.getMapper(MediaEntityMapper::class.java).modelToEntity(media)

        assertEquals("http://localhost/somewhere", mediaEntity.location)
    }

    @Test
    fun mediaEntityToMedia_validData_validData() {
        val mediaEntity = MediaEntity()
        with(mediaEntity) {
            id = UUID.randomUUID()
            name = "name"
            location = "http://localhost/somewhere"
            type = "MP4"
        }

        val media = Mappers.getMapper(MediaEntityMapper::class.java).entityToModel(mediaEntity)

        assertEquals(URI.create("http://localhost/somewhere"), media.location)
    }
}