import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  `maven-publish`
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
  }
}

group = "io.github.jakex7.peek"
version = "0.2.0"

repositories {
  mavenCentral()
}

gradlePlugin {
  plugins {
    create("peekGlanceFork") {
      id = "io.github.jakex7.peek.glance-fork"
      implementationClass = "io.github.jakex7.peek.gradle.PeekGlanceForkPlugin"
      displayName = "Peek Glance fork resolver"
      description = "Selects and validates the Glance AppWidget fork required by Peek extensions."
    }
  }
}

dependencies {
  testImplementation(kotlin("test-junit"))
  testImplementation("junit:junit:4.13.2")
}

tasks.test {
  useJUnit()
}
