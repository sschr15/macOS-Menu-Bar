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

//    modRuntimeOnly("maven.modrinth:sodium:mc1.21.1-0.8.13-fabric")
//    modImplementation("maven.modrinth:iris:1.8.14-beta.1+1.21.1-fabric")
    modImplementation("dev.emi:emi-fabric:$emiVersion")
    modImplementation("com.terraformersmc:modmenu:11.0.5")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.116.18+1.21.1")

    include("net.fabricmc.fabric-api:fabric-resource-loader-v0:1.3.1+5b5275af19")
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
