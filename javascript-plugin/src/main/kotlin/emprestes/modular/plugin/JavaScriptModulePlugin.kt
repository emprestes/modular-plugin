package emprestes.modular.plugin

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.provideDelegate

class JavaScriptModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create("jsModules", ModularExtension::class.java).apply {
                tasks.register("showMeJSConfig") {
                    doLast {
                        println("JavaScript module plugin:")
                        println("       debug: ${if (debugMode) "on" else "off"}")
                    }
                }

                subprojects
                    .filter {
                        containsIn(
                            source = it.projectDir,
                            content = arrayOf("com.github.node-gradle.node"),
                        )
                    }.apply { println("> Modules JavaScript $size FOUND") }
                    .forEach {
                        it.apply(plugin = "maven-publish")
                        it.apply(plugin = "com.github.node-gradle.node")

                        it.extensions.apply {
                            configure(NodeExtension::class.java) {
                                val nodeDownload: String by properties
                                val nodeVersion: String by properties

                                version.set(nodeVersion)
                                download.set("on".equals(nodeDownload, true))
                            }
                        }

                        it.tasks.apply {
                            register("jsLint", NpmTask::class.java) {
                                args.addAll("run", "lint")
                            }
                            register("jsTest", NpmTask::class.java) {
                                args.addAll("run", "test")
                            }
                            register("jsTestE2E", NpmTask::class.java) {
                                args.addAll("run", "test:e2e")
                            }

                            register("install") {
                                dependsOn(
                                    "jsLint", "jsTest", "jsTestE2E"
                                )
                            }
                        }

                        if (debugMode) {
                            println("> >>>>>>> JavaScript ${it.name} LOADED")
                        }
                    }
            }
        }
    }
}
