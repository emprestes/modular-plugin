package emprestes.modular.plugin

import org.gradle.api.Project
import java.io.File

fun containsIn(
    project: Project,
    fileName: String = "build.gradle.kts",
    vararg content: String,
) = containsIn(project.projectDir, fileName, *content)

fun containsIn(
    source: File,
    fileName: String = "build.gradle.kts",
    vararg content: String,
) = source.listFiles { file -> file.name == fileName }
    ?.any { it.isFile && content.any(it.readText()::contains) }
    ?: false
