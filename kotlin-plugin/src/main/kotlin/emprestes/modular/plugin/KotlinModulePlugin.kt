package emprestes.modular.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.filter
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.lang.System.getenv
import java.util.Locale.getDefault

class KotlinModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create("kotlinModules", ModularExtension::class.java).apply {
                tasks.register("showMeKotlinConfig") {
                    doLast {
                        println("Kotlin module plugin:")
                        println("       debug: ${if (debugMode) "on" else "off"}")
                    }
                }

                subprojects
                    .filter {
                        containsIn(
                            source = it.projectDir,
                            content = arrayOf("org.springframework.boot", "id(\"kotlin\")"),
                        )
                    }.apply { println("> Modules Kotlin $size FOUND") }
                    .sortedBy { it.name }
                    .forEach {
                        val jdkVersion: String by project.properties
                        val jvmArgs: String by project.properties
                        val kotlinVersion: String by project.properties

                        it.apply(plugin = "java-library")
                        it.apply(plugin = "maven-publish")
                        // prefer modern IDs; fall back to legacy if unavailable
                        try {
                            it.apply(plugin = "org.jetbrains.kotlin.jvm")
                        } catch (_: Exception) {
                            it.apply(plugin = "kotlin")
                        }
                        try {
                            it.apply(plugin = "org.jetbrains.kotlin.plugin.spring")
                        } catch (_: Exception) {
                            it.apply(plugin = "kotlin-spring")
                        }
                        it.apply(plugin = "com.diffplug.spotless")
                        it.apply(plugin = "io.spring.dependency-management")

                        it.pluginManager.takeIf { manager -> manager.hasPlugin("org.springframework.boot") }?.apply {
                            it.dependencies.add(
                                "annotationProcessor",
                                "org.springframework.boot:spring-boot-configuration-processor"
                            )
                            println("Annotation processor ADDED to ${it.name}")
                        }

                        it.extensions.also { ext ->
                            ext.configure(JavaPluginExtension::class.java) {
                                toolchain {
                                    languageVersion.set(JavaLanguageVersion.of(jdkVersion))
                                }
                                withSourcesJar()
                                withJavadocJar()
                            }

                            ext.configure(KotlinJvmProjectExtension::class.java) {
                                compilerOptions {
                                    freeCompilerArgs.set(jvmArgs.split(","))
                                }
                                jvmToolchain(jdkVersion.toInt())
                            }

                            ext.configure(PublishingExtension::class.java) {
                                publications {
                                    create("maven", MavenPublication::class.java) {
                                        components.forEach(::from)
                                    }
                                }

                                repositories {
                                    it.findProperty("gprRepository")?.let { repo ->
                                        maven {
                                            name = "GitHubPackages"
                                            it.findProperty("gprOwner")
                                                ?.let { owner -> it.uri("https://maven.pkg.github.com/$owner/$repo") }
                                                ?.also { remote -> url = remote }

                                            credentials {
                                                username =
                                                    "${it.findProperty("gpr.actor") ?: getenv("GITHUB_ACTOR")}"
                                                password = it.findProperty("gpr.token")?.toString()
                                                    ?: getenv(
                                                        "GITHUB_%s_TOKEN".format(repo)
                                                            .uppercase(getDefault())
                                                    )
                                            }
                                        }
                                    }
                                }
                            }

                            ext.configure(SpotlessExtension::class.java) {
                                kotlin {
                                    ktlint()
                                    trimTrailingWhitespace()
                                    endWithNewline()
                                }
                            }
                        }

                        it.tasks.apply {
                            getByName("publishToMavenLocal")
                                .mustRunAfter("test")
                                .mustRunAfter("build")
                                .mustRunAfter("spotlessApply")
                                .mustRunAfter("clean")

                            register("install") {
                                dependsOn(
                                    "clean",
                                    "spotlessApply",
                                    "build",
                                    "test",
                                    "publishToMavenLocal",
                                )
                            }

                            register("printVersion") {
                                doLast { println(">> ${it.group}:${it.name}:${it.version}") }
                            }

                            withType(Test::class.java).configureEach {
                                useJUnitPlatform()
                                testLogging {
                                    events("skipped", "failed", "standard_error")
                                }
                            }

                            afterEvaluate {
                                withType(ProcessResources::class.java) {
                                    val charset: String by project.properties
                                    filteringCharset = charset
                                    filesMatching("**/*.yml") {
                                        filter(
                                            ReplaceTokens::class,
                                            "tokens" to
                                                    mapOf(
                                                        "appVersion" to "${project.version}",
                                                        "appName" to project.name,
                                                        "kotlinVersion" to kotlinVersion
                                                    )
                                        )
                                    }
                                }
                            }
                        }

                        if (debugMode) {
                            println("> >>>>>>> Kotlin ${it.name} LOADED")
                        }
                    }
            }
        }
    }
}
