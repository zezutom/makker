plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jsonschema2dataclass") version "4.2.0"
    jacoco
}

jsonSchema2Pojo {
    targetPackage.set("com.tomaszezula.makker.make.api")
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
}

val jacksonVersion: String by project
val ktorVersion: String by project

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation(("io.ktor:ktor-client-mock:$ktorVersion"))
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))
    reports {
        xml.required.set(true)
    }
    dependsOn(tasks.test) // tests are required to run before generating the report
}
