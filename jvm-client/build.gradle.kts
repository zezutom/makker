plugins {
    kotlin("jvm")
}

val coroutinesVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation(project(":common"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
