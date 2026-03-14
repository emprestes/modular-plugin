# modular-plugin

Modular Gradle plugins for multi-module projects.

## Modules

| Module | Plugin ID | Description |
|--------|-----------|-------------|
| `shared` | — | Common utilities (`ModularExtension`, `containsIn`) |
| `loader-plugin` | `emprestes.modular.load` | Settings plugin that auto-discovers modules |
| `install-plugin` | `emprestes.modular` | Registers `install` tasks for submodules |
| `kotlin-plugin` | `emprestes.modular.kotlin` | Configures Kotlin/JVM subprojects |
| `javascript-plugin` | `emprestes.modular.javascript` | Configures Node.js/JS subprojects |
| `spring-boot-plugin` | `emprestes.modular.spring-boot` | Discovers Spring Boot modules |

## Build

```bash
./gradlew clean build
```
