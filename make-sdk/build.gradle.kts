buildscript {
    dependencies {
        classpath("org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:1.1.1")
    }
}

val jackson_version: String by project
val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jsonschema2dataclass")
}

jsonSchema2Pojo {
    targetPackage.set("com.langnerd.makker.make.api")
    sourceType.set("jsonschema")
    source.setFrom(files("${project.rootDir}/make-sdk/src/main/resources/json"))
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
    generateBuilders.set(true)
}

application {
    mainClass.set("com.langnerd.makker.ApplicationKt")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}