# modular-plugin

Modular Gradle plugins for multi-module projects.

## Modules

| Module | Plugin ID | Description |
|--------|-----------|-------------|
| `shared` | — | Common utilities (`ModularExtension`, `containsIn`) |
| `loader-plugin` | `modular.load` | Settings plugin that auto-discovers modules |
| `install-plugin` | `modular` | Registers `install` tasks for submodules |
| `kotlin-plugin` | `modular.kotlin` | Configures Kotlin/JVM subprojects |
| `javascript-plugin` | `modular.javascript` | Configures Node.js/JS subprojects |
| `spring-boot-plugin` | `modular.spring-boot` | Discovers Spring Boot modules |

## Build

```bash
./gradlew clean build
```
