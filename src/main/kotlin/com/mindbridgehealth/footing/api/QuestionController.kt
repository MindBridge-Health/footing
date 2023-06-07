package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.api.dto.QuestionResponseDto
import com.mindbridgehealth.footing.service.QuestionService
import com.mindbridgehealth.footing.service.model.Question
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/questions")
class QuestionController(val service: QuestionService) {


    @GetMapping("/{id}")

    fun get(@PathVariable id: String): Question {
        return service.findQuestionById(id).orElseThrow()
    }
    @PostMapping("/")
    fun post(@RequestBody question: Question): String {
        return service.save(question)
    }

    @PutMapping("/{id}")
    fun put(@RequestBody question: Question, @PathVariable id: String): Question  {
        return service.update(id, question)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(id)
}