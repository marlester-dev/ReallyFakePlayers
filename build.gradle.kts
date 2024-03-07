plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.5.11"
  id("xyz.jpenilla.run-paper") version "2.2.2" // Adds runServer and runMojangMappedServer tasks for testing
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("dev.racci.slimjar") version "1.6.1"
}

group = "me.marlester"
val javaVersion = 17
val minecraftVersion = "1.20.4"
version = "3.0.4"
description = "Simulates next-level fake players on a minecraft server."

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

repositories {
  maven {
    url = uri("https://repo.racci.dev/releases")
  }
  mavenCentral {
    content {
      includeModule("org.projectlombok", "lombok")
      includeModule("com.google.inject", "guice")
      includeModule("org.javassist", "javassist")
      includeModule("net.bytebuddy", "byte-buddy-agent")
      includeModule("org.jetbrains", "annotations")
      includeModule("dev.dejvokep", "boosted-yaml")
      includeModule("io.github.miniplaceholders", "miniplaceholders")
      includeModule("xyz.jpenilla", "reflection-remapper")
    }
  }
  maven {
    url = uri("https://jitpack.io")
    content {
      includeModule("com.github.MilkBowl", "VaultAPI")
    }
  }
  maven {
    url = uri("https://repository.liferay.com/nexus/content/repositories/public/")
    content {
      includeGroup("com.github.GeyserMC") //required by mcprotocollib
    }
  }
  maven {
    url = uri("https://repo.opencollab.dev/maven-releases/")
    content {
      includeGroup("com.github.steveice10") //required by mcprotocollib
      includeGroup("org.cloudburstmc.math") //required by mcprotocollib
      includeGroup("com.nukkitx.fastutil") //required by mcprotocollib
    }
  }
}

val miniPlaceholdersVersion = "2.2.3"
dependencies {
  paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

  compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
    // We don't need an excess bukkit 1.13.1 dependency
    exclude(group = "org.bukkit", module = "bukkit")
  }
  compileOnly("io.github.miniplaceholders:miniplaceholders-api:$miniPlaceholdersVersion")
  compileOnly("org.jetbrains:annotations:24.0.1") // We're used to them

  val lombokVersion = "1.18.30"
  compileOnly("org.projectlombok:lombok:$lombokVersion")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")

  implementation("dev.racci.slimjar:slimjar:1.6.1")
  val guiceVersion = "7.0.0"
  slim("com.google.inject:guice:$guiceVersion")
  slim("com.google.inject.extensions:guice-assistedinject:$guiceVersion")
  slim("xyz.jpenilla:reflection-remapper:0.1.0")
  slim("com.github.steveice10:mcprotocollib:1.20.4-1")
  slim("org.javassist:javassist:3.30.2-GA")
  slim("net.bytebuddy:byte-buddy-agent:1.14.11")
  slim("dev.dejvokep:boosted-yaml-spigot:1.4")
}

private fun prependRelocationPrefix(pakage: String): String {
  return "me.marlester.rfp.relocated" + pakage
}

tasks {

  slimJar {
    relocate("xyz.jpenilla.reflectionremapper", prependRelocationPrefix("reflectionremapper"))
    relocate("dev.devjokep.boostedyaml", prependRelocationPrefix("boostedyml"))
    relocate("com.google", prependRelocationPrefix("google"))
    relocate("com.github.steveice10", prependRelocationPrefix("steveice10"))
    relocate("io.netty", prependRelocationPrefix("ionetty"))
    relocate("it.unimi.dsi.fastutil", prependRelocationPrefix("fastutil"))
    relocate("javax", prependRelocationPrefix("javax"))
    relocate("net.bytebuddy.agent", prependRelocationPrefix("bytebuddyagent"))
    relocate("net.fabricmc", prependRelocationPrefix("fabricmc"))
    relocate("javassist", prependRelocationPrefix("javaassist"))
    relocate("jakarta", prependRelocationPrefix("jakarta"))
    relocate("net.kyori", prependRelocationPrefix("kyori"))
  }

  // reobfJar automatically executes shadowJar in build.
  shadowJar {
    dependsOn(slimJar)
    relocate("io.github.slimjar", "me.marlester.rfp.relocslimjar")
    // Include a license file.
    from("LICENSE_reallyfakeplayers")

    mergeServiceFiles()
  }

  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  runServer {
    downloadPlugins {
      github("MiniPlaceholders", "MiniPlaceholders", miniPlaceholdersVersion,
              "MiniPlaceholders-Paper-$miniPlaceholdersVersion.jar")
    }
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(javaVersion)
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }

  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to minecraftVersion.split(".").let { "${it[0]}.${it[1]}" }
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
      expand(props)
    }
  }

}
