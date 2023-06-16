package com.mindbridgehealth.footing.configuration

import com.amazonaws.auth.AWSCredentialsProvider
import software.amazon.awssdk.services.sts.StsClient;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.mindbridgehealth.footing.api.dto.Auth0User
import com.mindbridgehealth.footing.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import software.amazon.awssdk.auth.credentials.*
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import java.util.*


// Unused for now. Not listening for Auth0 events
// Instead we try to create a user when a POST happens to Storyteller or Benefactor
// May revisit after pilot

//@Configuration
//@EnableSqs
//class SqsConfig(val userService: UserService) {
//
//    @Value("\${cloud.aws.region:us-east-1}")
//    private val awsRegion: String? = null
//
//    @Value("\${aws.iam.role-arn}")
//    lateinit var iamRoleArn: String
//
//    @Primary
//    @Bean
//    fun sqsClient(awsCredentialsProvider: AwsCredentialsProvider): SqsClient {
//        return SqsClient.builder()
//            .region(Region.of(awsRegion))
//            .credentialsProvider(awsCredentialsProvider)
//            .build()
//    }
//
//    @Bean
//    fun credentialsProvider(): AwsCredentialsProvider {
//        val defaultCredentialsProvider = DefaultCredentialsProvider.create()
//        return AwsCredentialsProvider {
//            val stsClient = StsClient.builder()
//                .region(Region.of(awsRegion))
//                .credentialsProvider(defaultCredentialsProvider)
//                .build()
//
//            val assumeRoleRequest = AssumeRoleRequest.builder()
//                .roleArn(iamRoleArn)
//                .roleSessionName("auth-sqs-session")
//                .build()
//
//            val response = stsClient.assumeRole(assumeRoleRequest)
//            val credentials = response.credentials()
//
//            StaticCredentialsProvider.create(
//                AwsBasicCredentials.create(
//                    credentials.accessKeyId(),
//                    credentials.secretAccessKey()
//                )
//            ).resolveCredentials()
//        }
//    }
//
//    @Bean
//    fun queueMessagingTemplate(
//        amazonSQSAsync: AmazonSQSAsync?
//    ): QueueMessagingTemplate? {
//        return QueueMessagingTemplate(amazonSQSAsync)
//    }
//
//    @Bean
//    fun simpleMessageListenerContainerFactory(amazonSQSAsync: AmazonSQSAsync?): SimpleMessageListenerContainerFactory? {
//        val factory = SimpleMessageListenerContainerFactory()
//        factory.setAmazonSqs(amazonSQSAsync)
//        factory.setAutoStartup(true)
//        factory.setMaxNumberOfMessages(10)
//        factory.setTaskExecutor(createDefaultTaskExecutor())
//        return factory
//    }
//
//    protected fun createDefaultTaskExecutor(): AsyncTaskExecutor? {
//        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
//        threadPoolTaskExecutor.setThreadNamePrefix("SQSExecutor - ")
//        threadPoolTaskExecutor.corePoolSize = 100
//        threadPoolTaskExecutor.maxPoolSize = 100
//        threadPoolTaskExecutor.queueCapacity = 2
//        threadPoolTaskExecutor.afterPropertiesSet()
//        return threadPoolTaskExecutor
//    }
//
//    @SqsListener("https://sqs.us-east-1.amazonaws.com/732842978186/Auth0Registration", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
//    fun receiveMessageFromQueue(message: Auth0User) {
//        // Process the received message
//        println("Received message from listener: $message")
//        userService.createUser(message)
//
//    }
//}
