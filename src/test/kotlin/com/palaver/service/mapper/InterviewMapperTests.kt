package com.palaver.service.mapper

import com.palaver.data.entity.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class InterviewMapperTests {

    @Test
    fun interviewEntityToInterview_validEntity_validModel() {

        val completedTime = Instant.now()
        val chronicler = ChroniclerEntity()
        chronicler.id = UUID.randomUUID()
        chronicler.lastname = "d"
        chronicler.middlename = "a"
        chronicler.firstname = "c"
        chronicler.ai = false
        val storyteller = StorytellerEntity()
        storyteller.id = UUID.randomUUID()
        storyteller.lastname = "d"
        storyteller.middlename = "a"
        storyteller.firstname = "c"
        storyteller.benefactors = mutableListOf()
        storyteller.preferredChronicler = chronicler
        val questionEntity = QuestionEntity()
        questionEntity.id = UUID.randomUUID()
        questionEntity.name = "question 1"
        questionEntity.isCustom = false
        questionEntity.text = "What is your favorite color?"
        val storyEntity = StoryEntity()
        storyEntity.id = UUID.randomUUID()
        storyEntity.name = "l"
        storyEntity.text = "t"
        storyEntity.storyteller = storyteller
        val interviewQuestionEntity = InterviewQuestionEntity()
        interviewQuestionEntity.id = UUID.randomUUID()
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

        val model = Mappers.getMapper(InterviewEntityMapper::class.java).entityToModel(interviewEntity)

        assertEquals(completedTime, model.timeCompleted)
        assertEquals("Interview 1", model.name)
        assertEquals(true, model.completed)
        assertNotNull(model.chronicler)
        assertNotNull(model.storyteller)
        assertNotNull(model.interviewQuestions)
        assert(model.interviewQuestions.isNotEmpty())

    }
}