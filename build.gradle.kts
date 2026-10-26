import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom") version "1.17-SNAPSHOT" apply false
    id("architectury-plugin") version "3.5-SNAPSHOT"
    id("com.gradleup.shadow") version "9.4.3" apply false
    java
}

val modVersion = property("mod_version").toString()
val mavenGroup = property("maven_group").toString()
val archivesBaseName = property("archives_base_name").toString()
val minecraftVersion = property("minecraft_version").toString()
val loaderVersion = property("fabric_version").toString()
val parchmentVersion = property("parchment_version").toString()
val emiVersion = property("emi_version").toString()

allprojects {
    version = modVersion
    group = mavenGroup
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "java")

    base {
        archivesName = archivesBaseName
    }

    extensions.configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
        runs {
            named("client") {
                jvmArguments.addAll(
                    "-Dmixin.debug=true",
                    "-Dmixin.debug.export=true"
                )
            }
        }
    }

    repositories {
        mavenCentral()
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
        exclusiveContent { 
            forRepository { maven("https://api.modrinth.com/maven") }
            filter { includeGroup("maven.modrinth") }
        }
        maven("https://repo.sleeping.town/")
        maven("https://maven.terraformersmc.com/")
    }
    
    dependencies {
        compileOnly("com.demonwav.mcdev:annotations:2.1.0")
    }

    tasks.processResources {
        inputs.property("version", modVersion)
        inputs.property("minecraft_version", minecraftVersion)
        inputs.property("fabric_version", loaderVersion)
        filteringCharset = "UTF-8"

        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
            expand(
                "version" to modVersion,
                "minecraft_version" to minecraftVersion,
                "loader_version" to loaderVersion
            )
        }
    }

    val targetJavaVersion = 17
    tasks.withType<JavaCompile> {
        // ensure that the encoding is set to UTF-8, no matter what the system default is
        // this fixes some edge cases with special characters not displaying correctly
        // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
        // If Javadoc is generated, this must be specified in that task too.
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
        }
        // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
        // if it is present.
        // If you remove this line, sources will not be generated.
        withSourcesJar()
    }

    tasks.jar {
        from("LICENSE") {
            rename { "${it}_$archiveBaseName" }
        }
    }
}
