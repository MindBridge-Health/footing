package com.mindbridgehealth.footing.data.client

import com.mindbridgehealth.footing.service.SmsNotificationService
import com.twilio.rest.studio.v2.flow.ExecutionCreator
import com.twilio.type.PhoneNumber
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class TwilioSnsService: SmsNotificationService {

    val logger = LoggerFactory.getLogger(this::class.java)
    val generalMessageFlowId = "FW11bebd1ed9beede70cbf003a811115b1"
    val interviewMessageFlowId = "FW11eb3d59f134dc3ef56211df72adc21d"
    override fun sendMessage(to: String, message: String) {
        sendTwilioReminder(generalMessageFlowId, to, HashMap())
    }

    override fun sendInterviewLink(to: String, name: String, question: String, interviewUrl: String, interviewAltId: String, interviewQuestionId: String) {
        val parameters = HashMap<String, Any>()
        parameters["firstname"] = name
        parameters["question"] = question
        parameters["interviewurl"] = interviewUrl
        parameters["interview_id"] = interviewAltId
        parameters["interview_question_id"] = interviewQuestionId

        sendTwilioReminder(interviewMessageFlowId, to, parameters)
    }

    private fun sendTwilioReminder(
        flowId: String,
        number: String,
        parameters: HashMap<String, Any>
    ) {
        val client =
            ExecutionCreator(flowId, PhoneNumber(number), PhoneNumber("+18443555050"))
        client.setParameters(parameters)
        val execution = client.create()
        logger.info("Kicked off Twilio Reminder, SID: ${execution.sid}")
    }


}