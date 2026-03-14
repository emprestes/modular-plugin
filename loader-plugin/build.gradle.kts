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
        create("moduleLoader") {
            id = "emprestes.modular.load"
            implementationClass = "emprestes.modular.plugin.LoaderModulePlugin"
        }
    }
}
