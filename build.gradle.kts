import org.ajoberstar.grgit.Grgit
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    jacoco
    `maven-publish`
    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

logger.lifecycle("""
*******************************************
 You are building LWCX!
 If you encounter trouble:
 1) Try running 'build' in a separate Gradle run
 2) Use gradlew and not gradle
 3) If you still need help, ask on Discord! https://discord.gg/PHpuzZS

 Output files will be in build/libs
*******************************************
""")

if (!project.hasProperty("gitCommitHash")) {
    apply(plugin = "org.ajoberstar.grgit")
    ext["gitCommitHash"] = try {
        Grgit.open(mapOf("currentDir" to project.rootDir))?.head()?.abbreviatedId
    } catch (e: Exception) {
        logger.warn("Error getting commit hash", e)

        "no.git.id"
    }
}

repositories {
    mavenCentral()

    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.glaremasters.me/repository/public/") }
    maven { url = uri("https://jitpack.io") }
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")

    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
    compileOnly("commons-lang:commons-lang:2.6")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.12")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.3.7")
    compileOnly("com.massivecraft:Factions:1.6.9.5-U0.4.9") { isTransitive = false }
    compileOnly("com.github.milkbowl:VaultAPI:1.7.1")
    "shadeOnly"("org.bstats:bstats-bukkit:3.0.0")

}

tasks.named<Copy>("processResources") {
    val internalVersion = "$version-${rootProject.ext["gitCommitHash"]}"
    inputs.property("internalVersion", internalVersion)

    filesMatching("plugin.yml") {
        expand("internalVersion" to internalVersion)
    }
}

tasks.named<Jar>("jar") {
    val projectVersion = project.version
    inputs.property("projectVersion", projectVersion)
    manifest {
        attributes("Implementation-Version" to projectVersion)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate("org.bstats", "com.griefcraft.shaded.bstats") {
            include(dependency("org.bstats:"))
        }
        relocate("com.mysql", "com.griefcraft.shaded.mysql") {
            include(dependency("mysql:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
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
