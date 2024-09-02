import java.util.*

plugins {
    id("java-library")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.glaremasters.me/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "1.21.1-R0.1-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldedit", name = "worldedit-core", version = "7.1.0")
    compileOnly(group = "com.sk89q.worldguard", name = "worldguard-bukkit", version = "7.0.0")
    compileOnly(group = "com.palmergames.bukkit.towny", name = "towny", version = "0.98.2.0")
    compileOnly(group = "com.massivecraft", name = "Factions", version = "1.6.9.5-U0.4.9") {
        isTransitive = false
    }
    compileOnly(group = "com.github.MilkBowl", name = "VaultAPI", version = "1.7.1")
    compileOnly(group = "com.google.guava", name = "guava", version = "23.0")
    implementation(group = "org.bstats", name = "bstats-bukkit", version = "3.0.2")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    jar {
        archiveClassifier.set("noshade")
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to project.version,
            )
        }
    }
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("${rootProject.name.uppercase(Locale.getDefault())}-${project.version}.jar")
        relocate("org.bstats", "${project.group}.${rootProject.name}.lib.bstats")
        manifest {
            attributes("paperweight-mappings-namespace" to "mojang")
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
