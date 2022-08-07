plugins {
    application
    kotlin("jvm")
}

val ktorVersion: String by project
val logbackVersion: String by project

application {
    mainClass.set("com.tomaszezula.makker.server.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    sourceSets {
        val commonMain by creating {
            dependencies {
                implementation(project(":make-adapter"))
            }
        }
        val main by getting {
            dependsOn(commonMain)
        }
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
    implementation(project(":jvm-client"))
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}
