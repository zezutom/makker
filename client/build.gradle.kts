val jackson_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val junit_version: String by project
val apache_http_client_version: String by project

plugins {
    kotlin("jvm")
    id("org.jsonschema2dataclass")
}

jsonSchema2Pojo {
    targetPackage.set("com.tomaszezula.makke.api")
    propertyWordDelimiters.set("_")
    includeGeneratedAnnotation.set(false)
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:${jackson_version}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jackson_version}")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.apache.httpcomponents:httpclient:$apache_http_client_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}