package emprestes.modular.plugin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class APITest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `containsIn returns true when file contains content`() {
        File(tempDir, "build.gradle.kts").writeText("""id("org.springframework.boot")""")
        assertTrue(containsIn(tempDir, "build.gradle.kts", "org.springframework.boot"))
    }

    @Test
    fun `containsIn returns false when file does not contain content`() {
        File(tempDir, "build.gradle.kts").writeText("apply plugin: 'java'")
        assertFalse(containsIn(tempDir, "build.gradle.kts", "org.springframework.boot"))
    }

    @Test
    fun `containsIn returns false when file does not exist`() {
        assertFalse(containsIn(tempDir, "build.gradle.kts", "anything"))
    }
}
