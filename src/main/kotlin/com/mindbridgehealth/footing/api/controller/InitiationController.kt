package com.mindbridgehealth.footing.api.controller

import com.mindbridgehealth.footing.service.InterviewService
import com.mindbridgehealth.footing.service.StorytellerService
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.mindbridgehealth.footing.service.util.PermissionValidator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/initiate")
class InitiationController(private val interviewService: InterviewService, private val storytellerService: StorytellerService) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/{id}")
    fun initiateInterview(@PathVariable id: String, @RequestParam("interview_question_id") interviewQuestionId: String, @RequestParam("rtel") mobile: String) {
        val decodedId = Base36Encoder.decodeAltId(id)


        val interview = interviewService.findInterviewByAltId(decodedId)
        val storyteller = interview.storyteller
        PermissionValidator.assertValidPermissions(interview, storyteller?.id)
        val interviewQuestions = interview.interviewQuestions
        if(interviewQuestions?.first()?.id == interviewQuestionId && storyteller?.mobile == mobile) {
            interviewService.initiateInterview(decodedId)
        } else {
            logger.warn("${interviewQuestions?.first()?.id} == $interviewQuestionId && ${storyteller?.mobile} == $mobile")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Data does not match")
        }

    }

}