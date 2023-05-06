plugins {
  application
  kotlin("jvm") version "1.8.20"
  id("io.ktor.plugin") version "2.3.0"
}

group = "io.github.japskiddin"
version = "1.0.0"
val mainClassName = "${project.group}.MainKt"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation(kotlin("stdlib"))
}

tasks {
  test {
    useJUnitPlatform()
  }
  jar {
    manifest {
      attributes(
        mapOf(
          "Main-Class" to mainClassName,
          "Implementation-Title" to project.name,
          "Implementation-Version" to project.version,
          "Specification-Version" to project.version)
      )
    }
  }
  shadowJar {
    manifest.inheritFrom(jar.get().manifest)
  }
}

kotlin {
  jvmToolchain(17)
}

application {
  mainClass.set(mainClassName)
}

ktor {
  fatJar {
    archiveFileName.set("kotlin-file-cipher.jar")
  }
}