package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class KotlinModulePluginTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin applies without error`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.modules.kotlin")
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")
        File(projectDir, "gradle.properties").writeText("""
            jdkVersion=21
            jvmArgs=-Xjsr305=strict
            kotlinVersion=2.2.20
            charset=UTF-8
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }

    @Test
    fun `plugin registers showMeKotlinConfig task`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.modules.kotlin")
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")
        File(projectDir, "gradle.properties").writeText("""
            jdkVersion=21
            jvmArgs=-Xjsr305=strict
            kotlinVersion=2.2.20
            charset=UTF-8
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("showMeKotlinConfig")
            .build()

        assertTrue(result.output.contains("Kotlin module plugin:"))
    }

    @Test
    fun `plugin creates kotlinModules extension`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.modules.kotlin")
            }
            kotlinModules {
                debugMode = true
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")
        File(projectDir, "gradle.properties").writeText("""
            jdkVersion=21
            jvmArgs=-Xjsr305=strict
            kotlinVersion=2.2.20
            charset=UTF-8
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }
}
