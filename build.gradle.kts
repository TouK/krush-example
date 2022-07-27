import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10"
}

val krush_version: String by project
val junit_version: String by project
val testcontainers_version: String by project

group = "pl.touk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("pl.touk.krush:krush-runtime:$krush_version")
    implementation("org.postgresql:postgresql:42.4.0")
    implementation(platform("org.testcontainers:testcontainers-bom:$testcontainers_version"))

    api("pl.touk.krush:krush-annotation-processor:$krush_version")
    kapt("pl.touk.krush:krush-annotation-processor:$krush_version")

    testApi("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testApi("org.assertj:assertj-core:3.23.1")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
    }
}
