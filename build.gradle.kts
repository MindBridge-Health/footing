import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import kotlin.io.path.Path
import org.siouan.frontendgradleplugin.infrastructure.gradle.InstallFrontendTask

kotlin {
    jvmToolchain(20)
}

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.siouan.frontend-jdk17") version "7.0.0"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "20"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<InstallFrontendTask>("installFrontend") {
    val ciPlatformPresent = providers.environmentVariable("CI").isPresent()
    val lockFilePath = "${project.projectDir}/src/main/resources/static/package-lock.json"
    val retainedMetadataFileNames: Set<String>
//    if (ciPlatformPresent) {
//        // If the host is a CI platform, execute a strict install of dependencies based on the lock file.
//        installScript.set("ci")
//        retainedMetadataFileNames = setOf(lockFilePath)
//    } else {
        // The naive configuration below allows to skip the task if the last successful execution did not change neither
        // the package.json file, nor the package-lock.json file, nor the node_modules directory. Any other scenario
        // where for example the lock file is regenerated will lead to another execution before the task is "up-to-date"
        // because the lock file is both an input and an output of the task.
        val acceptableMetadataFileNames = listOf(lockFilePath, "${project.projectDir}/src/main/resources/static/yarn.lock")
        retainedMetadataFileNames = mutableSetOf("${project.projectDir}/src/main/resources/static/package.json")
        for (acceptableMetadataFileName in acceptableMetadataFileNames) {
            if (Files.exists(Path(acceptableMetadataFileName))) {
                retainedMetadataFileNames.add(acceptableMetadataFileName)
                break
            }
        }
        outputs.file(lockFilePath).withPropertyName("lockFile")
//    }
    inputs.files(retainedMetadataFileNames).withPropertyName("metadataFiles")
    outputs.dir("${project.projectDir}/node_modules").withPropertyName("nodeModulesDirectory")
}

tasks.processResources {
    dependsOn("installFrontend")
}

frontend {
    nodeVersion.set("18.16.0")
    assembleScript.set("run build")
    cleanScript.set("run clean")
    verboseModeEnabled.set(true)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.0.2"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    testImplementation("junit:junit:4.13.1")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("javax.persistence:javax.persistence-api:2.2")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.twilio.sdk:twilio:9.8.0")
    implementation("software.amazon.awssdk:bom:2.20.109")
    implementation("software.amazon.awssdk:sts")
    implementation("com.amazonaws:aws-java-sdk-sqs:1.12.528")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.92")
    implementation("com.amazonaws:aws-java-sdk-sts:1.12.92")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.im4java:im4java:1.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.springframework.security:spring-security-test:6.1.1")
    testImplementation("com.h2database:h2:2.1.214")
}
