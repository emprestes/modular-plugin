plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("gradle-plugin", "2.2.20"))
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("moduleKotlin") {
            id = "emprestes.modular.kotlin"
            implementationClass = "emprestes.modular.plugin.KotlinModulePlugin"
        }
    }
}
