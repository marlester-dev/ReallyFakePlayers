import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.11"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

version = "3.0.5"
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

    paperLibrary("com.google.inject:guice:7.0.0")
    paperLibrary("com.google.inject.extensions:guice-assistedinject:7.0.0")
    paperLibrary("org.javassist:javassist:3.30.2-GA")
    paperLibrary("net.bytebuddy:byte-buddy-agent:1.14.12")
    paperLibrary("com.github.steveice10:mcprotocollib:1.20.4-1")
    paperLibrary("dev.dejvokep:boosted-yaml-spigot:1.4")
    paperLibrary("xyz.jpenilla:reflection-remapper:0.1.0")
    paperLibrary("com.github.Revxrsal.Lamp:common:3.1.9")
    paperLibrary("com.github.Revxrsal.Lamp:bukkit:3.1.9")
}

paper {
    loader = "me.marlester.rfp.ReallyFakePlayersLoader"
    generateLibrariesJson = true

    name = project.name
    version = project.version.toString()
    main = "me.marlester.rfp.ReallyFakePlayers"
    apiVersion = "1.20"
    authors = listOf("Marlester", "freethemice")
    description = project.description
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    website = "https://dev.bukkit.org/projects/really-fake-players"
    serverDependencies {
        register("MiniPlaceholders") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }

        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
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
