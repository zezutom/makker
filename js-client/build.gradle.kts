plugins {
    kotlin("js")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val ktorVersion: String by project

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            binaries.executable()
        }
    }
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
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-js:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}