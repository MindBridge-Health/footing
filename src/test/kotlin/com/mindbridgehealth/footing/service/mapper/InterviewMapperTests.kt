package com.mindbridgehealth.footing.service.mapper

import com.mindbridgehealth.footing.data.entity.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.math.floor
import kotlin.test.assertEquals

class InterviewMapperTests {

    @Test
    fun interviewEntityToInterview_validEntity_validModel() {

        val completedTime = Instant.now()
        val chronicler = ChroniclerEntity()
        chronicler.id = floor(Math.random() * 1000).toInt()
        chronicler.lastname = "d"
        chronicler.middlename = "a"
        chronicler.firstname = "c"
        chronicler.ai = false
        val storyteller = StorytellerEntity()
        storyteller.id = floor(Math.random() * 1000).toInt()
        storyteller.lastname = "d"
        storyteller.middlename = "a"
        storyteller.firstname = "c"
        storyteller.benefactors = mutableListOf()
        storyteller.preferredChronicler = chronicler
        val questionEntity = QuestionEntity()
        questionEntity.id = floor(Math.random() * 1000).toInt()
        questionEntity.name = "question 1"
        questionEntity.isCustom = false
        questionEntity.text = "What is your favorite color?"
        val storyEntity = StoryEntity()
        storyEntity.id = floor(Math.random() * 1000).toInt()
        storyEntity.name = "l"
        storyEntity.text = "t"
        storyEntity.storyteller = storyteller
        val interviewQuestionEntity = InterviewQuestionEntity()
        interviewQuestionEntity.id = floor(Math.random() * 1000).toInt()
        interviewQuestionEntity.name = "c"
        interviewQuestionEntity.interview =
            InterviewEntity("interview 1", Timestamp.from(completedTime), true, chronicler, storyteller)
        interviewQuestionEntity.question = questionEntity
        interviewQuestionEntity.story = storyEntity
        interviewQuestionEntity.skipped = false
        interviewQuestionEntity.completed = true


        val interviewEntity = InterviewEntity(
            "Interview 1",
            Timestamp.from(completedTime),
            true,
            chronicler,
            storyteller
        )
        interviewEntity.interviewQuestionData = arrayListOf(interviewQuestionEntity)

        var iem = com.mindbridgehealth.footing.service.mapper.InterviewEntityMapperImpl(
            TimeMapper(),
            com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapperImpl(
                BenefactorEntityMapperImpl(),
                com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl()
            ),
            com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl(),
            com.mindbridgehealth.footing.service.mapper.InterviewQuestionEntityMapperImpl(
                com.mindbridgehealth.footing.service.mapper.StorytellerEntityMapperImpl(
                    BenefactorEntityMapperImpl(),
                    com.mindbridgehealth.footing.service.mapper.ChroniclerEntityMapperImpl()
                ), com.mindbridgehealth.footing.service.mapper.QuestionEntityMapperImpl()
            )
        )
        val model = iem.entityToModel(interviewEntity)

        assertEquals(completedTime, model.timeCompleted)
        assertEquals("Interview 1", model.name)
        assertEquals(true, model.completed)
        assertNotNull(model.chronicler)
        assertNotNull(model.storyteller)
        assertNotNull(model.interviewQuestions)
        assert(model.interviewQuestions.isNotEmpty())

    }
}