import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.vanniktech.mavenPublish) apply false
}

val peekVersion: String = libs.versions.peek.get()

subprojects {
  group = "io.github.jakex7.peek"
  version = peekVersion
}

subprojects {
  plugins.withId("com.vanniktech.maven.publish") {
    extensions.configure<MavenPublishBaseExtension> {
      configure(AndroidSingleVariantLibrary(
        variant = "release",
        sourcesJar = true,
        publishJavadocJar = false,
      ))

      publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

      // Only sign when signing credentials are available (CI environment).
      if (project.findProperty("signingInMemoryKey") != null) {
        signAllPublications()
      }

      pom {
        name = project.name
        description = "Peek allows developers to build layouts for remote surfaces using a Jetpack Compose-style API."
        inceptionYear = "2026"
        url = "https://github.com/jakex7/peek"
        licenses {
          license {
            name = "The MIT License"
            url = "https://opensource.org/license/mit"
            distribution = "https://opensource.org/license/mit"
          }
        }
        developers {
          developer {
            id = "jakex7"
            name = "Jakub Grzywacz"
            url = "https://github.com/jakex7"
          }
        }
        scm {
          url = "https://github.com/jakex7/peek"
          connection = "scm:git:git://github.com/jakex7/peek.git"
          developerConnection = "scm:git:ssh://git@github.com/jakex7/peek.git"
        }
      }
    }
  }
}
