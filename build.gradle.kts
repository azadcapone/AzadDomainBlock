plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.formdev:flatlaf:3.4.1")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("SiteBlocker")
    archiveClassifier.set("")   // dosya adında "-all" olmasın
    archiveVersion.set("1.0")
    manifest {
        attributes["Main-Class"] = "Main"
    }
}

tasks.test {
    useJUnitPlatform()
}