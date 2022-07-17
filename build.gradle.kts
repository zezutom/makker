val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.7.10" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("org.jsonschema2dataclass") version "4.2.0" apply false
}

group = "com.tomaszezula.makker"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    }
}
