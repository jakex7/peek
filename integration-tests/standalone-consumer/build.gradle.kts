plugins {
    id("com.android.library") version "8.12.0"
    id("org.jetbrains.kotlin.android") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    id("io.github.jakex7.peek.glance-fork") version "0.2.0"
}

android {
    namespace = "io.github.jakex7.peek.integration.standalone"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("io.github.jakex7.peek:peek-notification:0.2.0")
    implementation("androidx.glance:glance-appwidget-multiprocess:1.3.0-alpha02")
}

tasks.register("verifyStandaloneResolution") {
    doLast {
        val componentIds = configurations
            .getByName("debugRuntimeClasspath")
            .incoming
            .resolutionResult
            .allComponents
            .map { it.id.displayName }

        check("io.github.jakex7.peek.forks:glance-appwidget:1.3.0-alpha02" in componentIds) {
            "The Peek Glance fork was not selected: $componentIds"
        }
        check("androidx.glance:glance-appwidget:1.3.0-alpha02" !in componentIds) {
            "The official appwidget artifact was not substituted: $componentIds"
        }
    }
}
