package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.QuestionService
import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.util.Base36Encoder
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.client.HttpClientErrorException
import java.util.*


class QuestionControllerTests {

    private val mockService = mockk<QuestionService>()
    private val mockJwt = mockk<Jwt>()

    private val questionController = QuestionController(mockService)

    @BeforeEach
    fun setUp() {
        clearMocks(mockService, mockJwt)
        every { mockJwt.subject } returns "user123"
        every { mockJwt.claims } returns mapOf("permissions" to listOf("read:userdata"))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test get existing question by id`() {
        // Arrange
        val questionId = "someId"
        val expectedQuestion = Question(id = questionId, text = "Test question", name = "Question", isCustom = false)
        every { mockService.findQuestionByAltId(any()) } returns Optional.of(expectedQuestion)

        // Act
        val response = questionController.get(mockJwt, Base36Encoder.encodeAltId(questionId))

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedQuestion, response.body)
        verify { mockService.findQuestionByAltId(eq(questionId)) }
    }

    @Test
    fun `test get non-existent question by id`() {
        // Arrange
        val questionId = "nonExistentId"
        every { mockService.findQuestionByAltId(any()) } returns Optional.empty()

        // Act & Assert
        assertThrows(HttpClientErrorException::class.java) {
            questionController.get(mockJwt, Base36Encoder.encodeAltId(questionId))
        }
        verify { mockService.findQuestionByAltId(questionId) }
    }

    @Test
    fun `test get question with insufficient permissions`() {
        // Arrange
        val questionId = "someId"
        val question = Question(id = questionId, text = "Test question", name = "Question", isCustom = false)
        question.ownerId = "someOwnerId"
        every { mockService.findQuestionByAltId(any()) } returns Optional.of(question)

        every { mockJwt.claims } returns  mapOf("permissions" to emptyList<String>())
        // Act & Assert
        assertThrows(HttpClientErrorException::class.java) {
            questionController.get(mockJwt, Base36Encoder.encodeAltId(questionId))
        }
        verify { mockService.findQuestionByAltId(questionId) }
    }

    @Test
    fun `test get question with null permissions`() {
        // Arrange
        val questionId = "someId"
        val question = Question(id = questionId, text = "Test question", name = "Question", isCustom = false)
        question.ownerId = "someOwnerId"
        every { mockService.findQuestionByAltId(any()) } returns Optional.of(question)

        every { mockJwt.claims } returns emptyMap()
        // Act & Assert
        assertThrows(HttpClientErrorException::class.java) {
            questionController.get(mockJwt, Base36Encoder.encodeAltId(questionId))
        }
        verify { mockService.findQuestionByAltId(questionId) }
    }

    @Test
    fun `test save question`() {
        // Arrange
        val inputQuestion = Question(id = Base36Encoder.encodeAltId("someId"), name = "New Question", text = "New question", isCustom = false)
        val expectedSavedQuestion = inputQuestion.copy(id = "someId")
        every { mockService.save(inputQuestion) } returns expectedSavedQuestion.id!!

        // Act
        val response = questionController.post(mockJwt, inputQuestion)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedSavedQuestion.id, response.body)
        verify { mockService.save(inputQuestion) }
    }

    @Test
    fun `test update question`() {
        // Arrange
        val questionId = "someId"
        val inputQuestion = Question(id = Base36Encoder.encodeAltId(questionId), name = "Updated Question", text = "Updated question", isCustom = false)
        val expectedUpdatedQuestion = inputQuestion.copy(id = questionId)
        every { mockService.update(questionId, inputQuestion) } returns expectedUpdatedQuestion

        // Act
        val response = questionController.put(mockJwt, inputQuestion, Base36Encoder.encodeAltId(questionId))

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        assertEquals(expectedUpdatedQuestion, response.body)
        verify { mockService.update(questionId, inputQuestion) }
    }

    @Test
    fun `test delete question`() {
        // Arrange
        val questionId = "someId"
        every { mockService.delete(questionId) } just Runs

        // Act
        val response = questionController.delete(Base36Encoder.encodeAltId(questionId))

        // Assert
        verify { mockService.delete(questionId) }
    }
}
