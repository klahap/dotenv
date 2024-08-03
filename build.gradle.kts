import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "1.9.20"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("org.jetbrains.kotlinx.kover") version "0.8.1"
}

val groupStr = "io.github.klahap.dotenv"
val gitRepo = "https://github.com/klahap/dotenv"
val pluginClass = "$groupStr.Plugin"

version = System.getenv("GIT_TAG_VERSION") ?: "1.0.0-SNAPSHOT"
group = groupStr

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

gradlePlugin {
    website = gitRepo
    vcsUrl = "$gitRepo.git"
    val generateFrappeDsl by plugins.creating {
        id = groupStr
        implementationClass = pluginClass
        displayName = "Dotenv Parser for Kotlin"
        description = "A plugin for parsing and loading environment variables from .env files"
        tags = listOf("env", "env file", "env parser", "Kotlin")
    }
}

kotlin {
    explicitApi()
    compilerOptions {
        allWarningsAsErrors = true
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    pluginClass,
                )
            }
        }
        verify {
            CoverageUnit.values().forEach { covUnit ->
                rule("minimal ${covUnit.name.lowercase()} coverage rate") {
                    minBound(100, coverageUnits = covUnit)
                }
            }
        }
    }
}
