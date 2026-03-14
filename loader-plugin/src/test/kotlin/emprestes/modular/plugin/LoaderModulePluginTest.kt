package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class LoaderModulePluginTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin applies without error`() {
        File(projectDir, "settings.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.modules.load")
            }
            
        """.trimIndent())
        File(projectDir, "gradle.properties").writeText("artifact=test-project\n")
        File(projectDir, "build.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertTrue(result.output.contains("Modules") || result.task(":help")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin discovers submodules`() {
        val subDir = File(projectDir, "submodule")
        subDir.mkdirs()
        File(subDir, "build.gradle.kts").writeText("")
        File(projectDir, "settings.gradle.kts").writeText("""
            plugins {
                id("emprestes.modular.modules.load")
            }
        """.trimIndent())
        File(projectDir, "gradle.properties").writeText("artifact=test-project\n")
        File(projectDir, "build.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("help")
            .build()

        assertTrue(result.output.contains("Modules"))
        assertTrue(result.output.contains("FOUND"))
    }
}
