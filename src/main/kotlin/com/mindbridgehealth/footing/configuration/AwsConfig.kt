package com.mindbridgehealth.footing.configuration

import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AwsConfig {

    @Value("\${spring.cloud.aws.credentials.sts.role-arn}")
    private lateinit var roleArn: String

    @Primary
    @Bean
    fun amazonS3Client(): AmazonS3 {
        val stsClient = AWSSecurityTokenServiceClient.builder().build()

        val assumeRoleRequest = AssumeRoleRequest()
            .withRoleArn(roleArn)
            .withRoleSessionName("FootingSession") // Provide a session name
            .withDurationSeconds(3600) // Specify the duration of assumed credentials

        val credentialsProvider = STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, "FootingSession")
            .withStsClient(stsClient)
            .withRoleSessionDurationSeconds(3600)
            .build()

        return AmazonS3Client.builder()
            .withCredentials(credentialsProvider)
            .build()
    }
}
