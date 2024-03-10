plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

version = "3.0.4"
description = "Simulates next-level fake players on a minecraft server."

repositories {
    mavenCentral()
    maven(url = uri("https://repo.opencollab.dev/maven-releases/"))
    maven(url = uri("https://jitpack.io"))
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.2.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("com.google.inject:guice:7.0.0")
    compileOnly("com.google.inject.extensions:guice-assistedinject:7.0.0")
    compileOnly("org.javassist:javassist:3.30.2-GA")
    compileOnly("net.bytebuddy:byte-buddy-agent:1.14.12")
    compileOnly("com.github.steveice10:mcprotocollib:1.20.4-1")
    compileOnly("dev.dejvokep:boosted-yaml-spigot:1.4")
    compileOnly("xyz.jpenilla:reflection-remapper:0.1.0")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
