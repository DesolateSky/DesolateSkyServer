plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.desolatesky"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.hypera.dev/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("net.minestom:minestom:2025.07.04-1.21.5")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.xerial:sqlite-jdbc:3.50.2.0")
    implementation("dev.hollowcube:schem:1.3.1")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("dev.lu15:luckperms-minestom:5.4-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.joml:joml:1.10.8")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes["Main-Class"] = "net.desolatesky.Main"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }

    register<Exec>("runJar") {
        group = "application"
        description = "Builds, copies, and runs the JAR from the run folder"

        dependsOn(build)

        doFirst {
            val jarFile = named<Jar>("jar").get().archiveFile.get().asFile
            val runDir = file("run")
            runDir.mkdirs()

            val targetJar = File(runDir, jarFile.name)
            jarFile.copyTo(targetJar, overwrite = true)
        }

        workingDir = file("run")

        standardInput = System.`in`
        isIgnoreExitValue = true
        commandLine("java", "-jar", named<Jar>("jar").get().archiveFileName.get())
    }

}
