/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

architectury {
    injectInjectables = false
    common(rootProject.property("enabled_platforms").toString().split(","))
}

val minecraftVersion = rootProject.property("minecraft_version").toString()
val parchmentVersion = rootProject.property("parchment_version").toString()
val emiVersion = rootProject.property("emi_version").toString()

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:0.20.1")
}
