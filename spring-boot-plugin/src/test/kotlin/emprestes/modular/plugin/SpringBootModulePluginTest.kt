package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SpringBootModulePluginTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin applies without error`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.spring-boot")
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }

    @Test
    fun `plugin creates springBootModules extension`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.spring-boot")
            }
            springBootModules {
                debugMode = true
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }

    @Test
    fun `plugin reports zero spring boot modules when none exist`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.spring-boot")
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertTrue(result.output.contains("Modules Spring Boot 0 FOUND"))
    }
}
