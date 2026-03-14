plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":shared"))
    implementation("com.github.node-gradle:gradle-node-plugin:7.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("moduleJavaScript") {
            id = "modular.modules.javascript"
            implementationClass = "emprestes.modular.plugin.JavaScriptModulePlugin"
        }
    }
}
