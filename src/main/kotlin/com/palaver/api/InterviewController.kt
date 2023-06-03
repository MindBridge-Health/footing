package com.palaver.api

import com.palaver.service.InterviewService
import com.palaver.service.model.Interview
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/interviews")
class InterviewController(val service: InterviewService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Interview {
        return service.getById(id)
    }

    @PostMapping("/storytellers/{storytellerId}/chroniclers/{chroniclerId}")
    fun post(@RequestBody interview: Interview, @PathVariable(name = "storytellerId") sid: UUID, @PathVariable(name = "chroniclerId") cid: UUID): UUID { //TODO: Needs to take Storyteller and Chronicler ids. meaning they must be created ahead of time.
        val returnedInterview = service.createInterview(interview.name, cid, sid, interview.interviewQuestions.map { iq -> iq.id!! }.toList(),true)
        return returnedInterview.id ?: throw Exception()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) {
        service.deleteInterview(id)
    }
}