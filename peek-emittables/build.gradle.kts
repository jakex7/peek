plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.vanniktech.mavenPublish)
}

android {
  namespace = "io.github.jakex7.peek.emittables"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }

  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  api(project(":peek-core"))
  api(project(":peek-remoteviews"))
  api(project(":peek-appwidget"))
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(project(":peek-testing"))
  testImplementation(libs.junit)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.ext.junit)
  testImplementation(libs.robolectric)
}
