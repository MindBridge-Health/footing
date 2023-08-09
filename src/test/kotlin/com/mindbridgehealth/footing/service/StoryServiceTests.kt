package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.StoryRepository
import com.mindbridgehealth.footing.service.entity.StoryEntity
import com.mindbridgehealth.footing.service.mapper.StoryEntityMapper
import com.mindbridgehealth.footing.service.model.Story
import io.mockk.*
import net.bytebuddy.asm.Advice.Argument
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StoryServiceTests {

    private val mockStoryDb = mockk<StoryRepository>()
    private val mockStoryMapper = mockk<StoryEntityMapper>()

    private val storyService = StoryService(mockStoryDb, mockStoryMapper)

    @Test
    fun `test saving story with no text or original text sets default text`() {
        // Arrange
        val inputStoryEntity = StoryEntity().apply {
            id = 1
        }

        every { mockStoryDb.save(any()) } returnsArgument 0

        // Act
        val savedStoryModel = storyService.saveEntity(inputStoryEntity)

        // Assert
        assertEquals("Not yet transcribed", savedStoryModel.text)
        verify { mockStoryDb.save(inputStoryEntity) }
        confirmVerified(mockStoryMapper, mockStoryDb)
    }
}
