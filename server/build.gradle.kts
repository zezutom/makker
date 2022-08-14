plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.openapi.generator")
    id("io.ktor.plugin")
    jacoco
}

val ktorVersion: String by project
val logbackVersion: String by project

application {
    mainClass.set("com.tomaszezula.makker.server.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

openApiGenerate {
    generatorName.set("openapi")
    inputSpec.set("$rootDir/server/src/main/resources/specs/api.yml")
    outputDir.set("$buildDir/generated")
    validateSpec.set(true)
}

ktor {
    fatJar {
        archiveFileName.set("makker-server.jar")
    }
}

dependencies {
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation(project(":common"))
    implementation(project(":jvm-client"))
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}

tasks.named("build") {
    dependsOn("openApiGenerate")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
