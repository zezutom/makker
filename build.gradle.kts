plugins {
    val kotlinVersion = "1.7.22"
    id("io.ktor.plugin") version "2.2.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
    id("org.jsonschema2dataclass") version "4.2.0" apply false
    kotlin("jvm") version kotlinVersion apply false
}

group = "com.langnerd.makker"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }
}

dependencies {
    project(":make-client")
    project(":make-sdk")
}

