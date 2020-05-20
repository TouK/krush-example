import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.60"
    kotlin("kapt") version "1.3.60"
}

val krush_version: String by project
val junit_version: String by project

group = "pl.touk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/exposed")
    maven(url = "https://philanthropist.touk.pl/nexus/content/repositories/releases")
    maven(url = "https://philanthropist.touk.pl/nexus/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("pl.touk.krush:runtime:$krush_version")
    implementation("com.h2database:h2:1.4.199")

    api("pl.touk.krush:annotation-processor:$krush_version")
    kapt("pl.touk.krush:annotation-processor:$krush_version")

    testApi("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testApi("org.assertj:assertj-core:3.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
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
