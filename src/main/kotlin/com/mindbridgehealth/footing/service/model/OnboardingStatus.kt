package com.mindbridgehealth.footing.service.model

enum class OnboardingStatus(val value: Int) {
    ONBOARDING_NOT_STARTED(0),
    ONBOARDING_STARTED(1),
    ONBOARDING_COMPLETED(99);

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}