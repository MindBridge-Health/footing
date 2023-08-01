package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.MindBridgeUserRepository
import com.mindbridgehealth.footing.data.repository.PreferredTimeRepository
import com.mindbridgehealth.footing.data.repository.StorytellerRepository
import com.mindbridgehealth.footing.service.entity.StorytellerEntity
import com.mindbridgehealth.footing.service.mapper.*
import com.mindbridgehealth.footing.service.model.*
import com.mindbridgehealth.footing.service.util.Base36Encoder
import com.ninjasquad.springmockk.MockkClear
import com.ninjasquad.springmockk.clear
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.Time
import java.time.DayOfWeek
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class StorytellerServiceTests {

    private val mockStorytellerDb = mockk<StorytellerRepository>()
    private val mockPreferredTimeDb = mockk<PreferredTimeRepository>()
    private val mockUserDb = mockk<MindBridgeUserRepository>()
    private val mockOrganizationService = mockk<OrganizationService>()

    private val preferredTimeMapperImpl = PreferredTimeMapperImpl()

    private val userMapper = UserMapper(OrganizationEntityMapperImpl())
    private val storytellerEntityMapperImpl = StorytellerEntityMapperImpl(
        BenefactorEntityMapperImpl(OrganizationEntityMapperImpl(), userMapper),
        ChroniclerEntityMapperImpl(OrganizationEntityMapperImpl(), userMapper),
        preferredTimeMapperImpl,
        OrganizationEntityMapperImpl(),
        UserMapperImpl()
    )

    @Test
    fun save_validDataNoExistingUser_success() {
        mockStorytellerDb.clear(MockkClear.BEFORE)
        mockPreferredTimeDb.clear(MockkClear.BEFORE)
        mockUserDb.clear(MockkClear.BEFORE)

        every { mockUserDb.findByAltId(any()) } returns Optional.empty()

        every { mockPreferredTimeDb.save(any()) } returnsArgument 0
        every { mockPreferredTimeDb.findByStorytellerAndDayOfWeekAndTime(any(), any(), any()) } returns Optional.empty()
        every { mockPreferredTimeDb.findAllByStoryteller(any())} returns emptyList()
        every { mockPreferredTimeDb.deleteAll(any()) } returnsArgument 0

        val storytellerCaptureSlot = slot<StorytellerEntity>()

        every { mockStorytellerDb.save(capture(storytellerCaptureSlot)) } answers { storytellerCaptureSlot.captured.apply { this.id = 1 }}

        val storytellerInput = Storyteller(null, "lastname", "firstname", "middle", "test@test.com", "1231231234", "text", listOf(
            Benefactor(null, "bLastname", "bFirstname", "bMiddlenamer", "btest@btest.com", "3213214321", null)
        ), Chronicler(null, "cLastname", "cFirstname", "cMiddlename", "ctest@ctest.com", "5435435432", true, null), null, ArrayList(), null)
        val preferredTime = PreferredTime(storytellerInput, Time(Instant.now().toEpochMilli()), DayOfWeek.WEDNESDAY)
        (storytellerInput.preferredTimes as ArrayList).add(preferredTime)

        val service = StorytellerService(mockStorytellerDb, mockPreferredTimeDb, storytellerEntityMapperImpl, preferredTimeMapperImpl, mockUserDb, mockOrganizationService)
        val storytellerOutput = service.save(storytellerInput,"auth0|648a23ab6ee6f0aa87941142")

        val capturedStoryTeller = storytellerCaptureSlot.captured
        assertEquals(storytellerInput.lastname, capturedStoryTeller.lastname)
        assertEquals(storytellerInput.firstname, capturedStoryTeller.firstname)
        assertEquals(storytellerInput.middlename, capturedStoryTeller.middlename)
        assertEquals("auth0|648a23ab6ee6f0aa87941142", capturedStoryTeller.altId)

        verify { mockPreferredTimeDb.save(any()) }

    }

    @Test
    fun save_validDataExistingUser_Exception() {
        mockStorytellerDb.clear(MockkClear.BEFORE)
        mockPreferredTimeDb.clear(MockkClear.BEFORE)
        mockUserDb.clear(MockkClear.BEFORE)

        every { mockUserDb.findByAltId(any()) } returns Optional.of(StorytellerEntity())

        every { mockPreferredTimeDb.save(any()) } returnsArgument 0
        every { mockPreferredTimeDb.findByStorytellerAndDayOfWeekAndTime(any(), any(), any()) } returns Optional.empty()

        val storytellerCaptureSlot = slot<StorytellerEntity>()

        every { mockStorytellerDb.save(capture(storytellerCaptureSlot)) } answers { storytellerCaptureSlot.captured.apply { this.id = 1 }}

        val storytellerInput = Storyteller(null, "lastname", "firstname", "middle", "test@test.com", "1231231234", "text", listOf(
            Benefactor(null, "bLastname", "bFirstname", "bMiddlenamer", "btest@btest.com", "3213214321", null)
        ), Chronicler(null, "cLastname", "cFirstname", "cMiddlename", "ctest@ctest.com", "5435435432", true, null), null, ArrayList(), null)
        val preferredTime = PreferredTime(storytellerInput, Time(Instant.now().toEpochMilli()), DayOfWeek.WEDNESDAY)
        (storytellerInput.preferredTimes as ArrayList).add(preferredTime)

        val service = StorytellerService(mockStorytellerDb, mockPreferredTimeDb, storytellerEntityMapperImpl, preferredTimeMapperImpl, mockUserDb, mockOrganizationService)
        assertThrows<Exception> {  service.save(storytellerInput, Base36Encoder.encodeAltId("auth0|648a23ab6ee6f0aa87941142")) }

    }

    @Test
    fun update_validDataExistingUser_Success() {
        mockStorytellerDb.clear(MockkClear.BEFORE)
        mockPreferredTimeDb.clear(MockkClear.BEFORE)
        mockUserDb.clear(MockkClear.BEFORE)

        every { mockStorytellerDb.findByAltIdAndIsActive(any(), true) } returns Optional.of(StorytellerEntity().apply {
            this.id = 1
            this.altId = "auth0|648a23ab6ee6f0aa87941142"
            this.isActive = true
        })

        every { mockPreferredTimeDb.save(any()) } returnsArgument 0
        every { mockPreferredTimeDb.findByStorytellerAndDayOfWeekAndTime(any(), any(), any()) } returns Optional.empty()
        every { mockPreferredTimeDb.findAllByStoryteller(any())} returns emptyList()
        every { mockPreferredTimeDb.deleteAll(any()) } returnsArgument 0

        val storytellerCaptureSlot = slot<StorytellerEntity>()

        every { mockStorytellerDb.save(capture(storytellerCaptureSlot)) } answers { storytellerCaptureSlot.captured.apply { this.id = 1 }}

        val storytellerInput = Storyteller(null, "lastname", "firstname", "middle", "test@test.com", "1231231234", "text", listOf(
            Benefactor(null, "bLastname", "bFirstname", "bMiddlenamer", "btest@btest.com", "3213214321", null)
        ), Chronicler(null, "cLastname", "cFirstname", "cMiddlename", "ctest@ctest.com", "5435435432", true, null), null, ArrayList(), null)
        val preferredTime = PreferredTime(storytellerInput, Time(Instant.now().toEpochMilli()), DayOfWeek.WEDNESDAY)
        (storytellerInput.preferredTimes as ArrayList).add(preferredTime)

        val service = StorytellerService(mockStorytellerDb, mockPreferredTimeDb, storytellerEntityMapperImpl, preferredTimeMapperImpl, mockUserDb, mockOrganizationService)
        val storytellerOutput = service.update(storytellerInput, Base36Encoder.encodeAltId("auth0|648a23ab6ee6f0aa87941142"))


        val capturedStoryteller = storytellerCaptureSlot.captured
        assertEquals("auth0|648a23ab6ee6f0aa87941142", capturedStoryteller.altId)
        assertEquals(storytellerInput.lastname, capturedStoryteller.lastname)
        assertEquals(storytellerInput.firstname, capturedStoryteller.firstname)
        assertEquals(storytellerInput.middlename, capturedStoryteller.middlename)
        assertEquals(storytellerInput.email, capturedStoryteller.email)
        assertEquals(storytellerInput.mobile, capturedStoryteller.mobile)
        assertEquals(storytellerInput.preferredChronicler?.lastname, capturedStoryteller.preferredChronicler?.lastname)
        assertEquals(1, capturedStoryteller.benefactors?.size)

        verify { mockPreferredTimeDb.save(any()) }

    }

    @Test
    fun update_validDataNoExistingUser_Exception() {
        mockStorytellerDb.clear(MockkClear.BEFORE)
        mockPreferredTimeDb.clear(MockkClear.BEFORE)
        mockUserDb.clear(MockkClear.BEFORE)

        every { mockStorytellerDb.findByAltIdAndIsActive(any(), true) } returns Optional.empty()

        every { mockPreferredTimeDb.save(any()) } returnsArgument 0
        every { mockPreferredTimeDb.findByStorytellerAndDayOfWeekAndTime(any(), any(), any()) } returns Optional.empty()

        val storytellerCaptureSlot = slot<StorytellerEntity>()

        every { mockStorytellerDb.save(capture(storytellerCaptureSlot)) } answers { storytellerCaptureSlot.captured.apply { this.id = 1 }}

        val storytellerInput = Storyteller(null, "lastname", "firstname", "middle", "test@test.com", "1231231234", "text", listOf(
            Benefactor(null, "bLastname", "bFirstname", "bMiddlenamer", "btest@btest.com", "3213214321", null)
        ), Chronicler(null, "cLastname", "cFirstname", "cMiddlename", "ctest@ctest.com", "5435435432", true, null), null, ArrayList(), null)
        val preferredTime = PreferredTime(storytellerInput, Time(Instant.now().toEpochMilli()), DayOfWeek.WEDNESDAY)
        (storytellerInput.preferredTimes as ArrayList).add(preferredTime)

        val service = StorytellerService(mockStorytellerDb, mockPreferredTimeDb, storytellerEntityMapperImpl, preferredTimeMapperImpl, mockUserDb, mockOrganizationService)
        assertThrows<Exception> {  service.update(storytellerInput, Base36Encoder.encodeAltId("auth0|648a23ab6ee6f0aa87941142")) }

    }

}