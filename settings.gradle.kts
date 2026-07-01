pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "peek"

include(
  ":peek-core",
  ":peek-runtime",
  ":peek-remoteviews",
  ":peek-notification",
  ":peek-appwidget",
  ":peek-emittables",
  ":peek-testing",
  ":sample",
)
