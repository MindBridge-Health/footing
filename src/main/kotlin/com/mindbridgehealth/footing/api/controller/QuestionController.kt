package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.QuestionService
import com.mindbridgehealth.footing.service.model.Question
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.PermissionValidator
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

        PermissionValidator.assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.OK).body(question)
    }

    @GetMapping("/storytellers/{storytellerId}/{questionId}")
    fun getOnBehalfOf(@AuthenticationPrincipal principal: Jwt, @PathVariable storytellerId: String, @PathVariable questionId: String): ResponseEntity<Question> {
            val optionalQuestion = service.findQuestionByAltId(Base36Encoder.decodeAltId(questionId))
            if(optionalQuestion.isEmpty) {
                throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
            }
            val question = optionalQuestion.get()

            PermissionValidator.assertValidPermissions(question, principal, storytellerId)
            return ResponseEntity.status(HttpStatus.OK).body(question)
    }

    @GetMapping("/")
    fun getAll(): Collection<Question> {
        return service.getAllQuestions()
    }
    @PostMapping("/")
    fun post(@AuthenticationPrincipal principal: Jwt, @RequestBody question: Question): ResponseEntity<String> {
        question.ownerId = principal.subject
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(question))
    }

    @PostMapping("/storytellers/{storytellerId}/")
    fun postOnBehalfOf(@AuthenticationPrincipal principal: Jwt, @PathVariable storytellerId: String, @RequestBody question: Question): ResponseEntity<String> {
        PermissionValidator.assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(question))
    }

    @PutMapping("/{id}")
    fun put(@AuthenticationPrincipal principal: Jwt, @RequestBody question: Question, @PathVariable id: String): ResponseEntity<Question>  {
        PermissionValidator.assertValidPermissions(question, principal)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.update(Base36Encoder.decodeAltId(id), question))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(Base36Encoder.decodeAltId(id))
}