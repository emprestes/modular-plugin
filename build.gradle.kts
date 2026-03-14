import net.researchgate.release.ReleaseExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import kotlin.jvm.java

plugins {
    id("net.researchgate.release") version "3.1.0"
    kotlin("jvm") version "2.2.20" apply false
}

extensions.configure(ReleaseExtension::class.java) {
    tagTemplate.set($$"v${version}")
    failOnSnapshotDependencies.set(false)
    git {
        requireBranch.set("master")
    }
}

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
        onlyIf { !project.version.toString().endsWith("-SNAPSHOT") }
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
                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()
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
