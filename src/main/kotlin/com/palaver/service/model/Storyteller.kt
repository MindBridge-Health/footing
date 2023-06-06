package com.palaver.service.model

import java.util.*

data class Storyteller(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    var contactMethod: String?,
    var benefactors: Collection<Benefactor>?,
    var preferredChronicler: Chronicler?,
    var onboardingStatus: OnboardingStatus? = OnboardingStatus.ONBOARDING_NOT_STARTED
) : User()