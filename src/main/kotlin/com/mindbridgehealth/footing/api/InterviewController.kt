package com.mindbridgehealth.footing.api

import com.mindbridgehealth.footing.api.dto.ScheduleInterviewResponseDto
import com.mindbridgehealth.footing.api.dto.mapper.ScheduledInterviewDtoMapper
import com.mindbridgehealth.footing.service.InterviewService
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.model.ScheduledInterview
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*
import kotlin.collections.Collection

@RestController
@RequestMapping("/interviews")
class InterviewController(val service: InterviewService, val dtoMapper: ScheduledInterviewDtoMapper) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Interview {
        return service.findInterviewById(id)
    }

    @PostMapping("/storytellers/{storytellerId}/chroniclers/{chroniclerId}")
    fun post(@RequestBody interview: Interview, @PathVariable(name = "storytellerId") sid: String, @PathVariable(name = "chroniclerId") cid: String): String {
        val returnedInterview = service.createInterview(interview.name, cid, sid, interview.interviewQuestions.map { iq -> iq.id!! }.toList(),true)
        return returnedInterview.id ?: throw Exception()
    }

    @PostMapping("/storytellers/{storytellerId}/scheduled/{id}")
    fun assignInterview(@PathVariable storytellerId: String, @PathVariable id: String, @RequestParam time: Instant?, @RequestParam name: String?): String {
        return service.scheduleInterview(storytellerId, id, time, name)
    }

    @GetMapping("/storytellers/{storytellerId}/scheduled:next/")
    fun getNextAssigned(@PathVariable storytellerId: String): ScheduleInterviewResponseDto? {
        val nextInterview = service.getNextInterview(storytellerId)
        if(nextInterview != null) {
            return dtoMapper.modelToDto(nextInterview)
        }

        return null
    }

    @GetMapping("/storytellers/{storytellerId}/scheduled:all/")
    fun getAllAssigned(@PathVariable storytellerId: String): Collection<ScheduleInterviewResponseDto>? {
        return service.getAllScheduledInterviews(storytellerId).map { s -> dtoMapper.modelToDto(s) }
    }

    @DeleteMapping("/scheduled/{scheduledInterviewId}")
    fun deleteScheduledInterview(@PathVariable scheduledInterviewId: String) {
        service.deleteScheduledInterview(scheduledInterviewId)
    }
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        service.deleteInterview(id)
    }
}