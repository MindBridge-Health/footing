package com.mindbridgehealth.footing.service

interface SmsNotificationService {

    fun sendMessage(to: String, message: String)

    fun sendInterviewLink(to: String, name: String, question: String, interviewUrl: String, interviewAltId: String, interviewQuestionId: String)
}