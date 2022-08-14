plugins {
    val kotlinVersion = "1.7.10"
    kotlin("js") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
    id("org.openapi.generator") version "6.0.1" apply false
    id("io.ktor.plugin") version "2.1.0" apply false
    base
    id("jacoco-report-aggregation")
}

group = "com.tomaszezula.makker"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }
}

dependencies {
    jacocoAggregation(project(":common"))
    jacocoAggregation(project(":jvm-client"))
    jacocoAggregation(project(":server"))
}

reporting {
    reports {
        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)
        }
    }
}

tasks.check {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}