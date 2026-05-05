
import net.researchgate.release.ReleaseExtension

plugins {
    id("net.researchgate.release") version "3.1.0"
    kotlin("jvm") version "2.3.20" apply false
}

extensions.configure(ReleaseExtension::class.java) {
    tagTemplate.set("v\$version")
    failOnSnapshotDependencies.set(false)
    git {
        requireBranch.set("master")
    }
}

val pluginIdsByModule = mapOf(
    "install-plugin" to "modular",
    "loader-plugin" to "loader",
    "kotlin-plugin" to "kotlin",
    "javascript-plugin" to "javascript",
    "spring-boot-plugin" to "spring-boot"
)

val packageDescriptions = mapOf(
    "shared" to "Shared utilities and extensions for Modular Gradle plugins.",
    "loader-plugin" to "Settings plugin that auto-discovers and includes multi-module projects.",
    "install-plugin" to "Plugin that registers install aggregation tasks for modular projects.",
    "kotlin-plugin" to "Plugin that standardizes Kotlin/JVM module conventions.",
    "javascript-plugin" to "Plugin that configures Node.js/JavaScript modules and tasks.",
    "spring-boot-plugin" to "Plugin that detects and configures Spring Boot modules."
)

subprojects {
    group = "${rootProject.property("group")}"
    version = "${rootProject.property("version")}"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    apply(plugin = "maven-publish")

    tasks.withType(Test::class.java).configureEach {
        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }

    tasks.withType(PublishToMavenRepository::class.java).configureEach {
        onlyIf {
            // Publish all plugin publications (including PluginMarkerMaven) for release versions.
            project.let {
                !"${it.version}".endsWith("- KotlinToolingVersion.Maturity.SNAPSHOT")
            }
        }
    }

    afterEvaluate {
        extensions.findByType(JavaPluginExtension::class.java)?.apply {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(24))
            }
            withSourcesJar()
            withJavadocJar()
        }

        extensions.configure(PublishingExtension::class.java) {
            publications {
                // Publish explicit `maven` publication only for the shared module.
                // Gradle plugin modules already provide plugin-specific publications.
                if (project.name == "shared" && findByName("maven") == null) {
                    create<MavenPublication>("maven") {
                        from(components.findByName("java"))
                        groupId = "${project.group}"
                        artifactId = project.name
                        version = "${project.version}"
                    }
                }

                // Use Gradle's standard PluginMarkerMaven publications (no custom markers).

                withType(MavenPublication::class.java).configureEach {
                    pom {
                        name.set("${project.group}:${project.name}")
                        description.set(
                            packageDescriptions[project.name]
                                ?: "Modular Gradle plugin module: ${project.name}."
                        )
                        url.set("https://github.com/emprestes/modular-plugin")
                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }
                        scm {
                            url.set("https://github.com/emprestes/modular-plugin")
                            connection.set("scm:git:https://github.com/emprestes/modular-plugin.git")
                            developerConnection.set("scm:git:ssh://git@github.com/emprestes/modular-plugin.git")
                        }
                    }
                }
            }
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/emprestes/modular-plugin")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user")?.toString()
                        password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.token")?.toString()
                    }
                }
            }
        }
    }
}
