import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.60"
    kotlin("kapt") version "1.3.60"
}

val junitVersion = "5.5.2"
val krushVersion = "0.2.0"

group = "pl.touk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/exposed")
    maven(url = "https://philanthropist.touk.pl/nexus/content/repositories/releases")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("pl.touk.krush:runtime:$krushVersion")
    implementation("com.h2database:h2:1.4.199")

    api("pl.touk.krush:annotation-processor:$krushVersion")
    kapt("pl.touk.krush:annotation-processor:$krushVersion")

    testApi("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testApi("org.assertj:assertj-core:3.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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
