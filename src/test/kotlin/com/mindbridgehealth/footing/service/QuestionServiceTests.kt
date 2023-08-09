package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.QuestionRepository
import com.mindbridgehealth.footing.service.entity.QuestionEntity
import com.mindbridgehealth.footing.service.mapper.QuestionEntityMapperImpl
import com.mindbridgehealth.footing.service.model.Question
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.Optional

@DataJpaTest
class QuestionServiceTests {

    @MockBean
    private lateinit var mockRepository: QuestionRepository

    private val questionMapper = QuestionEntityMapperImpl() // You may need to adjust this based on your actual implementation

    @Test
    fun `test find existing question by id`() {
        val questionId = 1
        val expectedQuestionEntity = QuestionEntity().apply {
            id = null
            text = "Test question"
            name = "Question"
            isCustom = false
        }
        `when`(mockRepository.findById(questionId)).thenReturn(Optional.of(expectedQuestionEntity))

        val service = QuestionService(mockRepository, questionMapper)
        val result = service.findQuestionById(questionId)

        assertTrue(result.isPresent)
        assertNotNull(result.get().id)
        assertEquals("Test question", result.get().text)
    }

    @Test
    fun `test find non-existent question by id`() {
        val questionId = 1
        `when`(mockRepository.findById(questionId)).thenReturn(Optional.empty())

        val service = QuestionService(mockRepository, questionMapper)
        val result = service.findQuestionById(questionId)

        assertFalse(result.isPresent)
    }

    @Test
    fun `test save question`() {
        val inputQuestion = Question(id = null, text = "Test question", name = "Question", isCustom = false)
        val expectedSavedQuestionEntity = QuestionEntity().apply {
            id = 1
            text = "Test question"
            name = "Question"
            isCustom = false
        }
        `when`(mockRepository.save(any(QuestionEntity::class.java))).thenReturn(expectedSavedQuestionEntity)

        val service = QuestionService(mockRepository, questionMapper)
        val result = service.save(inputQuestion)

        assertNotNull(result) // Assuming your mapper returns the ID as a string
    }

    // Similar tests for other methods (e.g., update and delete) can be added
}
