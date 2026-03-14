package emprestes.modular.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class InstallModulePlugin : Plugin<Project> {

    private val modules = setOf("_", "-")

    private fun isModule(project: Project) = modules.any { project.name.startsWith(it) }

    override fun apply(target: Project) {
        target.subprojects
            .filter { isModule(it) }
            .forEach { module ->
                module.tasks.apply {
                    register("install") {
                        dependsOn(
                            module.subprojects
                                .filter { it.parent?.name === module.name }
                                .map { "${it.name}:install" }
                        )
                    }
                }
            }
    }
}
