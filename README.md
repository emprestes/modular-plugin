# modular-plugin

Modular Gradle plugins for multi-module projects.

## Modules

| Module | Plugin ID | Description |
|--------|-----------|-------------|
| `shared` | — | Common utilities (`ModularExtension`, `containsIn`) |
| `loader-plugin` | `emprestes.modular.modules.load` | Settings plugin that auto-discovers modules |
| `install-plugin` | `emprestes.modular.modules` | Registers `install` tasks for submodules |
| `kotlin-plugin` | `emprestes.modular.modules.kotlin` | Configures Kotlin/JVM subprojects |
| `javascript-plugin` | `emprestes.modular.modules.javascript` | Configures Node.js/JS subprojects |
| `spring-boot-plugin` | `emprestes.modular.modules.spring-boot` | Discovers Spring Boot modules |

## Build

```bash
./gradlew clean build
```
