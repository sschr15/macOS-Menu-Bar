/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("com.gradleup.shadow")
}

val emiVersion = rootProject.property("emi_version").toString()

architectury {
    platformSetupLoomIde()
    fabric()
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
    getByName("developmentFabric").extendsFrom(common.get())
}

val minecraftVersion = rootProject.property("minecraft_version").toString()
val parchmentVersion = rootProject.property("parchment_version").toString()

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    })

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":common", configuration = "transformProductionFabric"))

    modImplementation("net.fabricmc:fabric-loader:0.20.1")

    modCompileOnly("maven.modrinth:iris:1.7.6+1.20.1")
    modCompileOnly("dev.emi:emi-fabric:$emiVersion")
    modImplementation("com.terraformersmc:modmenu:7.2.2")

    modImplementation(include("net.fabricmc.fabric-api:fabric-resource-loader-v0:0.11.12+fb82e9d777")!!)
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
