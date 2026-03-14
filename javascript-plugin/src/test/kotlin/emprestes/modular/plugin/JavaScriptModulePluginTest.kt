package emprestes.modular.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class JavaScriptModulePluginTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun `plugin applies without error`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("modular.javascript")
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
    fun `plugin registers showMeJSConfig task`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("modular.javascript")
            }
        """.trimIndent())
        File(projectDir, "settings.gradle.kts").writeText("")

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("showMeJSConfig")
            .build()

        assertTrue(result.output.contains("JavaScript module plugin:"))
    }

    @Test
    fun `plugin creates jsModules extension`() {
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("modular.javascript")
            }
            jsModules {
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
}
