package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.api.dto.ScheduleInterviewResponseDto
import com.mindbridgehealth.footing.api.dto.mapper.ScheduledInterviewDtoMapper
import com.mindbridgehealth.footing.service.InterviewService
import com.mindbridgehealth.footing.service.QuestionService
import com.mindbridgehealth.footing.service.model.Interview
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.PermissionValidator
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import kotlin.collections.Collection

@RestController
@RequestMapping("/api/v1/interviews")
class InterviewController(val service: InterviewService, val questionService: QuestionService, val dtoMapper: ScheduledInterviewDtoMapper) {

    @GetMapping("/{id}")
    fun get(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String): Interview {
        val interview = service.findInterviewByAltId(Base36Encoder.decodeAltId(id))
        PermissionValidator.assertValidPermissions(interview, principal)
        return interview
    }

    @GetMapping("/storytellers/")
    fun getAllInterviewsForSelf(@AuthenticationPrincipal principal: Jwt): Collection<Interview> {
        println(principal.subject)
        return service.findByStorytellerAltId(principal.subject)
    }

    @GetMapping("/storytellers/{storytellerId}")
    fun getAllInterviewsOnBehalfOf(@PathVariable(name = "storytellerId") sid: String): Collection<Interview> {
        return service.findByStorytellerAltId(Base36Encoder.decodeAltId(sid))
    }

    @GetMapping("/storytellers/scheduled/next")
    fun getNextAssignedForSelf(@AuthenticationPrincipal principal: Jwt): ScheduleInterviewResponseDto? {
        val nextInterview = service.getNextInterview(Base36Encoder.decodeAltId(principal.subject))
        if(nextInterview != null) {
            return dtoMapper.modelToDto(nextInterview)
        }

        return null
    }

    @GetMapping("/storytellers/{storytellerId}/scheduled/next")
    fun getNextAssignedOnBehalfOf(@PathVariable storytellerId: String): ScheduleInterviewResponseDto? {
        val nextInterview = service.getNextInterview(Base36Encoder.decodeAltId(storytellerId))
        if(nextInterview != null) {
            return dtoMapper.modelToDto(nextInterview)
        }

        return null
    }

    @GetMapping("/storytellers/scheduled/all")
    fun getAllAssignedForSelf(@AuthenticationPrincipal principal: Jwt): Collection<ScheduleInterviewResponseDto>? {

        val allScheduledInterviews = service.getAllScheduledInterviews(principal.subject)
        return allScheduledInterviews.map {
            val dto = dtoMapper.modelToDto(it)
            val q = (it.interview.interviewQuestions as List)[0].questionId?.let { it1 ->
                questionService.findQuestionByAltId(
                    it1
                )
            }
            if (q != null && q.isPresent) {
                dto.question = q.get()
            }
            dto
        }
    }

    @GetMapping("/storytellers/{storytellerId}/scheduled/all")
    fun getAllAssignedOnBehalfOf(@AuthenticationPrincipal principal: Jwt, @PathVariable storytellerId: String): Collection<ScheduleInterviewResponseDto>? {
        return service.getAllScheduledInterviews(Base36Encoder.decodeAltId(storytellerId)).map {
            val dto = dtoMapper.modelToDto(it)
            val q = (it.interview.interviewQuestions as List)[0].questionId?.let { it1 ->
                questionService.findQuestionByAltId(
                    Base36Encoder.decodeAltId(it1)
                )
            }
            if (q != null && q.isPresent) {
                dto.question = q.get()
            }
            dto
        }
    }

    @PostMapping("/storytellers/{storytellerId}/chroniclers/{chroniclerId}")
    fun postOnBehalfOf(@RequestBody interview: Interview, @PathVariable(name = "storytellerId") sid: String, @PathVariable(name = "chroniclerId") cid: String): String {
        val returnedInterview = service.createInterview(interview.name, Base36Encoder.decodeAltId(cid), Base36Encoder.decodeAltId(sid), interview.interviewQuestions?.map { iq -> Base36Encoder.decodeAltId(iq.id!!) }?.toList())
        return returnedInterview.id ?: throw Exception()
    }

    @PostMapping("/storytellers/scheduled/")
    fun scheduleInterviewForSelf(@AuthenticationPrincipal principal: Jwt, @RequestParam questionId: String, @RequestParam time: Instant?, @RequestParam name: String?, @RequestParam append: Boolean = false): String {
        return innerScheduleInterview(principal.subject, questionId, time, name, append)
    }

    @PostMapping("/storytellers/{storytellerId}/scheduled/")
    fun scheduleInterviewOnBehalfOf(@AuthenticationPrincipal principal: Jwt, @PathVariable(name = "storytellerId") sid: String, @RequestParam questionId: String, @RequestParam time: Instant?, @RequestParam name: String?, @RequestParam append: Boolean = false): String {
        return innerScheduleInterview(Base36Encoder.decodeAltId(sid), questionId, time, name, append)
    }

    fun innerScheduleInterview(sid: String, questionId: String, time: Instant?, name: String?, append: Boolean = false): String {
        if(append && time != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't specify append and a time")
        }
        val returnedInterview = service.createInterview(name ?: "unnamed", Base36Encoder.decodeAltId("c1"), sid, listOf(Base36Encoder.decodeAltId(questionId)))
        return returnedInterview.id?.let { service.scheduleInterview(sid, Base36Encoder.decodeAltId(it), time, name, append) } ?: "Error"
    }

    @PostMapping("/storytellers/{storytellerId}/scheduled/{interviewId}")
    fun assignInterview(@PathVariable storytellerId: String, @PathVariable interviewId: String, @RequestParam time: Instant?, @RequestParam name: String?, @RequestParam append: Boolean = false): String {
        if(append && time != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't specify append and a time")
        }
        return Base36Encoder.encodeAltId(service.scheduleInterview(Base36Encoder.decodeAltId(storytellerId), Base36Encoder.decodeAltId(interviewId), time, name, append))
    }


    @DeleteMapping("/storytellers/{storytellerId}/scheduled/{scheduledInterviewId}")
    fun deleteScheduledInterview(@PathVariable storytellerId: String, @PathVariable scheduledInterviewId: String) {
        service.deleteScheduledInterview(Base36Encoder.decodeAltId(scheduledInterviewId))
    }

    @DeleteMapping("/{id}")
    fun delete(@AuthenticationPrincipal principal: Jwt, @PathVariable id: String) {
        val interview = service.findInterviewByAltId(Base36Encoder.decodeAltId(id))
        PermissionValidator.assertValidPermissions(interview, principal)
        service.deleteInterview(Base36Encoder.decodeAltId(id))
    }
}