package com.mindbridgehealth.footing.data.client

import org.slf4j.LoggerFactory
import com.twilio.rest.studio.v2.flow.ExecutionCreator
import com.twilio.type.PhoneNumber
import org.springframework.stereotype.Service

@Service
class TwilioCallService {

    val logger = LoggerFactory.getLogger(this::class.java)
    val callFlowId = "FWf36bdbc2a99b0dacc2fc3120ab433202"

    fun triggerInterviewCall(firstname: String, phoneNumber: String, question: String, interviewId: String, interviewQuestionId: String) {

        val parameters = mapOf(
            "firstname" to firstname,
            "question" to question,
            "interview_id" to interviewId,
            "interview_question_id" to interviewQuestionId
        )

        val client =
            ExecutionCreator(callFlowId, PhoneNumber(phoneNumber), PhoneNumber("+18443555050"))
        client.setParameters(parameters)
        val execution = client.create()
        logger.info("Triggered Twilio Call, SID: ${execution.sid}")

    }

}