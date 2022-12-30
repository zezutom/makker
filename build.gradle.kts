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
    val kotlinVersion = "1.7.22"
    kotlin("jvm") version kotlinVersion
    id("io.ktor.plugin") version "2.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    id("org.jsonschema2dataclass") version "4.2.0"
}

jsonSchema2Pojo {
    targetPackage.set("com.tomaszezula.makker.make.api")
    sourceType.set("jsonschema")
    source.setFrom(files("${project.rootDir}/src/main/resources/json"))
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
    generateBuilders.set(true)
}

group = "com.langnerd.makker"
version = "0.0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
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

//    jacocoAggregation(project(":common"))
//    jacocoAggregation(project(":jvm-client"))
//    jacocoAggregation(project(":server"))
}

//reporting {
//    reports {
//        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
//            testType.set(TestSuiteType.UNIT_TEST)
//        }
//    }
//}
//
//tasks.check {
//    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
//}