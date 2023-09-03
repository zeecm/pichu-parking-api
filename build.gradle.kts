plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "org.pichugroup"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.4"
    val mockkVersion = "1.13.5"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-test-dispatcher:$ktorVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("Handler.kt")
}

tasks.register<Zip>("buildZip") {
    into("lib") {
        from(tasks.getByName<Jar>("jar"))
        from(configurations.getByName("runtimeClasspath"))
    }
}