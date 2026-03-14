package emprestes.modular.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File

class LoaderModulePlugin : Plugin<Settings> {
    private fun loadAllWith(name: String) =
        File("./")
            .walkTopDown()
            .filter { it.name.contains("build.gradle.kts") }
            .map { it.path.substringBefore("/build.gradle.kts") }
            .map { it.substringAfter("./") }
            .map { it.replace('/', ':') }
            .filter { it.isNotBlank() }
            .filter { it != "." }
            .filter { it != "plugins" }
            .sorted()
            .map { it to it }
            .map { (key, value) -> key to value.removePrefix("-") }
            .map { (key, value) -> key to value.removePrefix("_") }
            .map { (key, value) -> key to value.replace("_", "") }
            .map { (key, value) -> key to value.replace(":model", "") }
            .map { (key, value) -> key to value.replace("-model", "") }
            .map { (key, value) -> key to value.replace(":", "-") }
            .map { (key, value) -> key to value.replace("--", "-") }
            .map { (key, value) -> key to value.replace("web-admin", "webadmin") }
            .map { (key, value) -> key to value.replace("web-app", "webapp") }
            .map { (key, value) -> key to value.replace("web-webmvc", "webmvc") }
            .map { (key, value) -> key to value.replace("web-webflux", "webflux") }
            .map { (key, value) -> key to value.replace("web-client", "webclient") }
            .map { (key, value) -> key to value.replace("web-service", "webservice") }
            .map { (key, value) -> key to "$name-$value" }
            .toMap()

    override fun apply(settings: Settings) {
        with(settings) {
            val artifact: String by settings

            loadAllWith(artifact).also { modules ->
                include(modules.map { (module, _) -> module })

                with(rootProject) {
                    name = artifact
                    modules.forEach { (module, name) -> findProject(":$module")?.name = name }
                }

                println("> Modules ${modules.size} FOUND")
            }
        }
    }
}
