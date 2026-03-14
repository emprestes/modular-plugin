plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("moduleInstall") {
            id = "modular.modules"
            implementationClass = "emprestes.modular.plugin.InstallModulePlugin"
        }
    }
}
