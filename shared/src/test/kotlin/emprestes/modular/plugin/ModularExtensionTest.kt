package emprestes.modular.plugin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ModularExtensionTest {

    @Test
    fun `default debugMode is false`() {
        val ext = ModularExtension()
        assertFalse(ext.debugMode)
    }

    @Test
    fun `debugMode can be set`() {
        val ext = ModularExtension()
        ext.debugMode = true
        assertTrue(ext.debugMode)
    }
}
