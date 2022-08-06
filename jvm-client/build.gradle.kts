plugins {
    kotlin("jvm")
}

val coroutinesVersion: String by project

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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}
