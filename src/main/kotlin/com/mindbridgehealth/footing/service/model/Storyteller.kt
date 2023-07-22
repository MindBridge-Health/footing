package com.mindbridgehealth.footing.service.model

import com.mindbridgehealth.footing.service.util.Base36Encoder
import java.util.*

data class Storyteller(
    override var id: String?,
    override var lastname: String?,
    override var firstname: String?,
    override var middlename: String?,
    override var email: String?,
    override var mobile: String?,
    var contactMethod: String?,
    var benefactors: Collection<Benefactor>?,
    var preferredChronicler: Chronicler?,
    var onboardingStatus: OnboardingStatus? = OnboardingStatus.ONBOARDING_NOT_STARTED,
    var preferredTimes: Collection<PreferredTime>?,
    override var organization: Organization?
) : User()