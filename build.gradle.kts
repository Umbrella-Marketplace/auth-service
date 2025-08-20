import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "dev.zornov.market.auth"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.kafka)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    // Kotlin
    implementation(libs.kotlin.reflect)

    // JSON
    implementation(libs.jackson.core)
    implementation(libs.jackson.module.kotlin)

    // JWT
    implementation(libs.nimbus.jose.jwt)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
    }
}
