package com.palaver.api

import com.palaver.api.dto.QuestionResponseDto
import com.palaver.service.QuestionService
import com.palaver.service.model.Question
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
    fun post(@RequestBody question: Question): Question {
        return service.save(question)
    }

    @PutMapping("/{id}")
    fun put(@RequestBody question: Question, @PathVariable id: String): Question  {
        return service.update(id, question)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(id)
}