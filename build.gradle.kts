plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.pichugroup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}