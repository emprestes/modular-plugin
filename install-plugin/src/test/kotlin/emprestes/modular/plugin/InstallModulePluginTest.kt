package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class InstallModulePluginTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin applies without error`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular")
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
    fun `plugin registers install task for module subprojects`() {
        val moduleDir = File(projectDir, "_mymodule")
        moduleDir.mkdirs()
        File(moduleDir, "build.gradle.kts").writeText("")

        File(projectDir, "settings.gradle.kts").writeText("""include("_mymodule")""")
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular")
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(":_mymodule:tasks", "--all")
            .build()

        assertTrue(result.output.contains("install"))
    }
}
