package com.palaver.service

import com.palaver.data.InterviewRepository
import com.palaver.data.StorytellerRepository
import com.palaver.data.generated.ChroniclerData
import com.palaver.data.generated.InterviewData
import com.palaver.data.generated.StorytellerData
import com.palaver.service.model.*

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrElse


@Service
class InterviewService(val interviewDb : com.palaver.data.InterviewRepository, val storytellerDb: com.palaver.data.StorytellerRepository) {
//    fun findByStorytellerId(storytellerId: UUID): Collection<Interview> {
//        val storytellerQueryData = StorytellerData()
//        storytellerQueryData.id = storytellerId
//        val interviewResultData = interviewDb.findByStorytellerId(storytellerQueryData)
//        return interviewResultData.stream().map { interviewResult ->
//            val id = interviewResult.id
//            val name = interviewResult.name.orEmpty()
//            val storyteller = Storyteller(interviewResult.storytellerId!!.id, interviewResult.storytellerId!!.name)
//            val chronicler = Chronicler(interviewResult.chroniclerId!!.id, interviewResult.chroniclerId!!.name.orEmpty(), false) //todo: map isAi properly
//            val timeCompleted = interviewResult.timeCompleted?.toInstant()
//            val completed = interviewResult.completed!!.or(false)
//            val questionData = interviewResult.interviewQuestionData.stream().map { question ->
//                InterviewQuestion(question.id,
//                        question.name,
//                        Question(question.id, question.name, question.question!!.text, question.question!!.custom),
//                        "",
//                        false,
//                        false)
//            }.toList()
//            Interview(id, name, storyteller, chronicler, timeCompleted, completed, questionData)
//        }.toList()
//    }


    //TODO Build mappers


    fun createInterview(name: String, storyteller: Storyteller, chronicler: Chronicler, save: Boolean) : Interview {
        if(storyteller.id == null) {
            throw IllegalArgumentException("storyteller id required to create interview")
        }
        val dbStoryteller = storytellerDb.findById(storyteller.id!!).getOrElse { throw Exception("Storyteller was not found, unable to create interview") }
        val dbChronicler = ChroniclerData()
        val interview = InterviewData(name, null, false,  dbChronicler, dbStoryteller)
        if(save) {
            interviewDb.save(interview)
        }

        return Interview(interview.id, name, storyteller, chronicler, null, false, Collections.emptyList())
    }

}