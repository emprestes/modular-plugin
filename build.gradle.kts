import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

subprojects {
    group = "${rootProject.property("group")}"
    version = "${rootProject.property("version")}"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    afterEvaluate {
        extensions.findByType(JavaPluginExtension::class.java)?.apply {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(24))
            }
        }
    }
}
