package com.mindbridgehealth.footing.service

import com.mindbridgehealth.footing.data.repository.SignatureRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
class SignatureExpiryTask(private val signatureRepository: SignatureRepository) {

    companion object {
        const val TOKEN_VALIDITY_IN_SECONDS: Long = 60 * 60 * 24
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 * * * * *")
    fun performTask() {
        val timestamp24HoursAgo = Timestamp.from(Instant.now().plusSeconds(-1L * TOKEN_VALIDITY_IN_SECONDS))
        val expiredSignatures = signatureRepository.findAllByIssuedBefore(timestamp24HoursAgo)
        expiredSignatures.forEach {
            logger.debug("expiring signature ${it.signature} issued ${it.issued}")
            signatureRepository.delete(it)
        }
    }
}