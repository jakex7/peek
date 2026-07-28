plugins {
    id("com.android.library") version "8.12.0"
    id("org.jetbrains.kotlin.android") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    id("io.github.jakex7.peek.glance-fork") version "0.2.0"
}

android {
    namespace = "io.github.jakex7.peek.integration.standalone"
    compileSdk = 37

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
    implementation("androidx.glance:glance-appwidget-multiprocess:1.2.0-rc01")
}

tasks.register("verifyStandaloneResolution") {
    doLast {
        val runtimeClasspath = configurations.getByName("debugRuntimeClasspath")
        runtimeClasspath.resolve()

        val componentIds = runtimeClasspath
            .incoming
            .resolutionResult
            .allComponents
            .map { it.id.displayName }

        check("io.github.jakex7.peek.forks:glance-appwidget:1.2.0-rc01" in componentIds) {
            "The Peek Glance fork was not selected: $componentIds"
        }
        check("androidx.glance:glance-appwidget:1.2.0-rc01" !in componentIds) {
            "The official appwidget artifact was not substituted: $componentIds"
        }
    }
}
