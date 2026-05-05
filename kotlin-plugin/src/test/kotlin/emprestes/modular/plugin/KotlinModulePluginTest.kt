package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.writeText

class KotlinModulePluginTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `configures kotlin modules adds boot processor and ignores non matching modules`() {
        val projectDir = File(tempDir, "kotlin-plugin-fixture").apply { mkdirs() }
        val initScript = writeInitScript(projectDir)
        writeSettings(projectDir)
        writeGradleProperties(projectDir)
        writeRootBuild(projectDir)
        writeAppBuild(projectDir)
        writeDocsBuild(projectDir)

        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("assertAll", "--stacktrace", "--init-script", initScript.absolutePath)
            .withDebug(true)
            .forwardOutput()
            .build()
    }

    private fun writeInitScript(projectDir: File): File {
        return Path(projectDir.path, "init.gradle.kts").toFile().apply {
            writeText(
                """
                allprojects {
                    buildscript {
                        repositories {
                            mavenLocal()
                            mavenCentral()
                            gradlePluginPortal()
                        }
                        dependencies {
                            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
                            classpath("org.jetbrains.kotlin:kotlin-allopen:2.3.20")
                            classpath("org.springframework.boot:spring-boot-gradle-plugin:4.0.3")
                            classpath("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.6")
                            classpath("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
                        }
                    }
                }
                """.trimIndent(),
            )
        }
    }

    private fun writeSettings(projectDir: File) {
        Path(projectDir.path, "settings.gradle.kts").writeText(
            """
            pluginManagement {
                repositories {
                    mavenLocal()
                    mavenCentral()
                    gradlePluginPortal()
                }
                resolutionStrategy {
                    eachPlugin {
                        if (requested.id.id == "org.jetbrains.kotlin.plugin.spring") {
                            useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
                        }
                        if (requested.id.id == "org.jetbrains.kotlin.jvm") {
                            useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
                        }
                    }
                }
            }
            rootProject.name = "kotlin-plugin-fixture"
            include("app", "docs")
            """.trimIndent(),
        )
    }

    private fun writeGradleProperties(projectDir: File) {
        Path(projectDir.path, "gradle.properties").writeText(
            """
            jdkVersion=24
            jvmArgs=-Xmx1g,-Xms1g
            kotlinVersion=2.3.20
            charset=UTF-8
            """.trimIndent(),
        )
    }

    private fun writeRootBuild(projectDir: File) {
        Path(projectDir.path, "build.gradle.kts").writeText(
            """
            buildscript {
                repositories {
                    mavenLocal()
                    mavenCentral()
                    gradlePluginPortal()
                }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
                    classpath("org.jetbrains.kotlin:kotlin-allopen:2.3.20")
                    classpath("org.springframework.boot:spring-boot-gradle-plugin:4.0.3")
                    classpath("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.6")
                    classpath("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
                }
            }

            plugins {
                id("emprestes.modular.kotlin")
            }

            subprojects {
                buildscript {
                    repositories {
                        mavenLocal()
                        mavenCentral()
                        gradlePluginPortal()
                    }
                    dependencies {
                        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
                        classpath("org.jetbrains.kotlin:kotlin-allopen:2.3.20")
                        classpath("org.springframework.boot:spring-boot-gradle-plugin:4.0.3")
                        classpath("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.6")
                        classpath("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
                    }
                }
            }

            tasks.register("assertAll") {
                dependsOn(":app:assertKotlinModuleConfig", ":docs:assertIgnored")
            }
            """.trimIndent(),
        )
    }

    private fun writeAppBuild(projectDir: File) {
        File(projectDir, "app").mkdirs()
        Path(projectDir.path, "app/build.gradle.kts").writeText(
            """
            plugins {
                id("kotlin")
            }

            tasks.register("assertKotlinModuleConfig") {
                doLast {
                    check(plugins.hasPlugin("kotlin")) { "kotlin plugin should be applied" }
                    check(plugins.hasPlugin("kotlin-spring")) { "kotlin-spring plugin should be applied" }
                    check(plugins.hasPlugin("java-library")) { "java-library plugin should be applied" }
                    check(plugins.hasPlugin("maven-publish")) { "maven-publish plugin should be applied" }
                    check(plugins.hasPlugin("com.diffplug.spotless")) { "spotless plugin should be applied" }
                    check(plugins.hasPlugin("io.spring.dependency-management")) { "dependency-management plugin should be applied" }

                    val javaExt = extensions.getByType(org.gradle.api.plugins.JavaPluginExtension::class.java)
                    check(javaExt.toolchain.languageVersion.get().asInt() == 24) { "toolchain should target Java 24" }

                    val kotlinExt = extensions.getByType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class.java)
                    check(kotlinExt.compilerOptions.freeCompilerArgs.get() == listOf("-Xmx1g", "-Xms1g")) { "compiler args should match jvmArgs" }

                    val testTask = tasks.withType(org.gradle.api.tasks.testing.Test::class.java).single()
                    check(testTask.testLogging.events.containsAll(
                        listOf(
                            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
                        )
                    )) { "test logging events should include skipped/failed/standard_error" }

                    val processResources = tasks.withType(org.gradle.language.jvm.tasks.ProcessResources::class.java).single()
                    check(processResources.filteringCharset == "UTF-8") { "filteringCharset should come from charset property" }

                    val install = tasks.findByName("install") ?: error("install task should be created")
                    val installDeps = install.dependsOn.map { it.toString() }
                    check(installDeps.any { it.contains("clean") }) { "install should depend on clean" }
                    check(installDeps.any { it.contains("spotlessApply") }) { "install should depend on spotlessApply" }
                    check(installDeps.any { it.contains("build") }) { "install should depend on build" }
                    check(installDeps.any { it.contains("test") }) { "install should depend on test" }
                    check(installDeps.any { it.contains("publishToMavenLocal") }) { "install should depend on publishToMavenLocal" }

                    check(tasks.findByName("printVersion") != null) { "printVersion task should be registered" }
                }
            }
            """.trimIndent(),
        )
    }

    private fun writeBootBuild(projectDir: File) {
        File(projectDir, "boot-app").mkdirs()
        Path(projectDir.path, "boot-app/build.gradle.kts").writeText(
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "2.3.20"
                id("org.jetbrains.kotlin.plugin.spring") version "2.3.20"
                id("org.springframework.boot") version "4.0.3"
            }

            tasks.register("assertBootProcessor") {
                doLast {
                    val annotationProcessorDeps = configurations.getByName("annotationProcessor").dependencies
                    check(annotationProcessorDeps.any { it.group == "org.springframework.boot" && it.name == "spring-boot-configuration-processor" }) {
                        "Boot modules should receive the configuration processor"
                    }
                }
            }
            """.trimIndent(),
        )
    }

    private fun writeDocsBuild(projectDir: File) {
        File(projectDir, "docs").mkdirs()
        Path(projectDir.path, "docs/build.gradle.kts").writeText(
            """
            // intentionally empty

            tasks.register("assertIgnored") {
                doLast {
                    check(!plugins.hasPlugin("kotlin")) { "Non-kotlin modules should be ignored" }
                    check(tasks.findByName("install") == null) { "Ignored modules should not get install task" }
                }
            }
            """.trimIndent(),
        )
    }
}
