plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":shared"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("moduleSpringBoot") {
            id = "emprestes.modular.spring-boot"
            implementationClass = "emprestes.modular.plugin.SpringBootModulePlugin"
        }
    }
}
