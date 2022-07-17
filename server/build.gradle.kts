val jackson_version: String by project
val javax_validation_version: String by project
val joda_time_version: String by project
val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("org.jsonschema2dataclass")
}

jsonSchema2Pojo {
    targetPackage.set("com.tomaszezula.makke.api")
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
}

application {
    mainClass.set("com.tomaszezula.makke.Application")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jackson_version}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jackson_version}")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("javax.validation:validation-api:${javax_validation_version}")
    implementation("joda-time:joda-time:${joda_time_version}")

    implementation(project(":client"))

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
