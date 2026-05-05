plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("gradle-plugin", "2.3.20"))
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.springframework.boot:spring-boot-gradle-plugin:4.0.3")
    testImplementation("io.spring.gradle:dependency-management-plugin:1.1.6")
    testImplementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
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
