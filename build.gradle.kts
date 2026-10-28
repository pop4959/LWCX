import java.util.*

plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.6.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.glaremasters.me/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.74-stable")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.1.0") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.1.0") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.0") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.0") {
        isTransitive = false
    }
    compileOnly("com.palmergames.bukkit.towny:towny:0.98.2.0")
    compileOnly("com.massivecraft:Factions:1.6.9.5-U0.4.9")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("org.apache.commons:commons-lang3:3.19.0")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
    compileOnly("com.google.guava:guava:33.3.1-jre")
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

val paperBuild = configurations.compileOnly.get().dependencies.any { it.group == "io.papermc.paper" }

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 25
    }
    jar {
        archiveClassifier.set("noshade")
    }
    processResources {
        val properties = mapOf(
            "version" to project.version,
        )
        inputs.properties(properties)
        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("${rootProject.name.uppercase(Locale.getDefault())}-${if (paperBuild) "Paper" else "Bukkit"}-${project.version}.jar")
        relocate("org.bstats", "${project.group}.${rootProject.name}.lib.bstats")
        manifest {
            attributes(
                "paperweight-mappings-namespace" to "mojang",
                "lwc-platform" to if (paperBuild) "paper" else "spigot",
            )
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

publishing {
    repositories {
        if (project.hasProperty("mavenUsername") && project.hasProperty("mavenPassword")) {
            maven {
                credentials {
                    username = "${project.property("mavenUsername")}"
                    password = "${project.property("mavenPassword")}"
                }
                url = uri("https://repo.codemc.io/repository/maven-releases/")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
            from(components["java"])
        }
    }
}
