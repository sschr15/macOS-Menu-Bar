/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common = configurations.register("common") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
val shadowBundle = configurations.register("shadowBundle") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    compileClasspath.get().extendsFrom(common.get())
    runtimeClasspath.get().extendsFrom(common.get())
    getByName("developmentNeoForge").extendsFrom(common.get())
}

repositories {
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
}

val minecraftVersion = rootProject.property("minecraft_version").toString()
val parchmentVersion = rootProject.property("parchment_version").toString()
val neoforgeVersion = rootProject.property("neoforge_version").toString()

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    })

    neoForge("net.neoforged:neoforge:$neoforgeVersion")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":common", configuration = "transformProductionNeoForge"))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(mapOf("version" to inputs.properties["version"]))
    }
}

tasks {
    shadowJar {
        configurations.set(listOf(shadowBundle.get()))
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        inputFile = shadowJar.get().archiveFile
    }
}
