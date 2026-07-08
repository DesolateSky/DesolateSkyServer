plugins {
    id("java")
}

group = "com.fisherl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.hypera.dev/snapshots/")
    maven("https://mvn.everbuild.org/public")
}

dependencies {
    implementation("net.minestom:minestom:2026.05.17-1.21.11")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("dev.lu15:luckperms-minestom:5.5-SNAPSHOT+26.1.1-everbuild")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.joml:joml:1.10.8")
    implementation("de.bsommerfeld.pathetic:engine:5.5.2")
    implementation("de.bsommerfeld.pathetic:api:5.5.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.test {
    useJUnitPlatform()
}