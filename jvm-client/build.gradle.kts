plugins {
    kotlin("jvm")
}

val coroutinesVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project

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
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
