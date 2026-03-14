package emprestes.modular.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpringBootModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            extensions.create("springBootModules", ModularExtension::class.java).apply {
                subprojects
                    .filter {
                        containsIn(
                            source = it.projectDir,
                            content = arrayOf("id(\"org.springframework.boot\")"),
                        )
                    }.apply { println("> Modules Spring Boot $size FOUND") }
                    .filter { debugMode }
                    .forEach { println(">> ${it.name}") }
            }
        }
    }
}
