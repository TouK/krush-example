import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("kapt") version "1.3.61"
}

val junitVersion = "5.5.2"
val krushVersion = "0.1.0"
val requeryVersion = "1.6.1"

group = "pl.touk"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/exposed")
    maven(url = "https://philanthropist.touk.pl/nexus/content/repositories/releases")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.h2database:h2:1.4.199")
    implementation("io.requery:requery:$requeryVersion")
    implementation("io.requery:requery-kotlin:$requeryVersion")
    implementation("javax.persistence:javax.persistence-api:2.2")

    kapt("io.requery:requery-processor:$requeryVersion")

    testApi("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testApi("org.assertj:assertj-core:3.13.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("ch.qos.logback:logback-classic:1.2.3")
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
