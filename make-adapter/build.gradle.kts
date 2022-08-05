plugins {
    kotlin("multiplatform")
}

val ktorVersion: String by project

kotlin {
    jvm {}
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
    }

}

