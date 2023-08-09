package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.QuestionService
import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.util.Base36Encoder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RestController
@RequestMapping("/api/v1/questions")
class QuestionController(val service: QuestionService) {


    @GetMapping("/{id}")

    fun get(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String): ResponseEntity<Question> {
        val optionalQuestion = service.findQuestionByAltId(Base36Encoder.decodeAltId(id))
        if(optionalQuestion.isEmpty) {
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
        val question = optionalQuestion.get()

        assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.OK).body(question)
    }

    @GetMapping("/")
    fun getAll(): Collection<Question> {
        return service.getAllQuestions()
    }
    @PostMapping("/")
    fun post(@AuthenticationPrincipal principal: Jwt, @RequestBody question: Question): ResponseEntity<String> {
        assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(question))
    }

    @PutMapping("/{id}")
    fun put(@AuthenticationPrincipal principal: Jwt, @RequestBody question: Question, @PathVariable id: String): ResponseEntity<Question>  {
        assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.update(Base36Encoder.decodeAltId(id), question))
    }

    private fun assertValidPermissions(
        question: Question,
        principal: Jwt
    ) {
        // Check if the Jwt subject matches the question's owner
        val permissions = principal.claims["permissions"] as List<*>?
        if ((permissions == null || !permissions.contains("read:userdata")) && (question.ownerId != null && principal.subject != question.ownerId)) {
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(Base36Encoder.decodeAltId(id))
}