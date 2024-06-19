import com.blamejared.Properties
import com.blamejared.Versions
import com.blamejared.gradle.mod.utils.GMUtils
import org.gradle.jvm.tasks.Jar
import java.nio.charset.StandardCharsets

plugins {
    base
    `java-library`
    idea
    `maven-publish`
}

base.archivesName.set("${Properties.NAME}-${project.name.lowercase()}-${Versions.MINECRAFT}")
version = GMUtils.updatingVersion(Versions.MOD)
group = Properties.GROUP

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_21.majorVersion))
    withSourcesJar()
    withJavadocJar()
}

@Suppress("UnstableApiUsage")
repositories {
    mavenCentral()
    maven("https://maven.blamejared.com/") {
        name = "BlameJared"
        content {
            includeGroupAndSubgroups("com.blamejared")
            includeGroupAndSubgroups("mezz.jei")
            includeGroupAndSubgroups("com.faux")
            includeGroupAndSubgroups("org.openzen")
        }
    }
    maven("https://repo.spongepowered.org/repository/maven-public/") {
        name = "Sponge"
        content {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    maven("https://maven.parchmentmc.org/") {
        name = "ParchmentMC"
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
}

setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it: String ->
    configurations.getByName(it).outgoing {
        capability("$group:${base.archivesName.get()}:$version")
        capability("$group:${Properties.MODID}-${project.name}-${Versions.MINECRAFT}:$version")
        capability("$group:${Properties.MODID}:$version")
    }
    publishing.publications {
        if (this is MavenPublication) {
            this.suppressPomMetadataWarningsFor(it)
        }
    }
}

tasks {
    named<JavaCompile>("compileJava").configure {
        options.encoding = StandardCharsets.UTF_8.toString()
        options.release.set(Versions.JAVA.toInt())
    }
    named<Javadoc>("javadoc").configure {
        options {
            encoding = StandardCharsets.UTF_8.toString()
            // Javadoc defines this specifically as StandardJavadocDocletOptions
            // but only has a getter for MinimalJavadocOptions, but let's just make sure to be safe
            if (this is StandardJavadocDocletOptions) {
                addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }
    named<ProcessResources>("processResources").configure {
        val properties = mapOf(
                "version" to project.version,
                "MOD" to Versions.MOD,
                "JAVA" to Versions.JAVA,
                "MINECRAFT" to Versions.MINECRAFT,
                "FABRIC_LOADER" to Versions.FABRIC_LOADER,
                "FABRIC" to Versions.FABRIC,
                "FORGE" to Versions.FORGE,
                "FORGE_LOADER" to Versions.FORGE_LOADER,
                "NEO_FORGE" to Versions.NEO_FORGE,
                "NEO_FORGE_LOADER" to Versions.NEO_FORGE_LOADER,
                "GROUP" to Properties.GROUP,
                "NAME" to Properties.NAME,
                "AUTHOR" to Properties.AUTHOR,
                "MODID" to Properties.MODID,
                "AVATAR" to Properties.AVATAR,
                "CURSE_PROJECT_ID" to Properties.CURSE_PROJECT_ID,
                "CURSE_HOMEPAGE" to Properties.CURSE_HOMEPAGE,
                "MODRINTH_PROJECT_ID" to Properties.MODRINTH_PROJECT_ID,
                "GIT_REPO" to Properties.GIT_REPO,
                "DESCRIPTION" to Properties.DESCRIPTION,
                "ITEM_ICON" to Properties.ITEM_ICON,
                "SEARCHABLES" to Versions.SEARCHABLES
        )
        inputs.properties(properties)
        filesMatching(setOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(properties)
        }
    }
    named<Jar>("jar").configure {
        from(project.rootProject.file("LICENSE"))
        manifest {
            attributes["Specification-Title"] = Properties.NAME
            attributes["Specification-Vendor"] = Properties.AUTHOR
            attributes["Specification-Version"] = archiveVersion
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = archiveVersion
            attributes["Implementation-Vendor"] = Properties.AUTHOR
            attributes["Built-On-Java"] = "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
            attributes["Build-On-Minecraft"] = Versions.MINECRAFT
        }
    }
}

@Suppress("UnstableApiUsage")
configurations {
    val library = register("library")
    val lor = register("localOnlyRuntime")
    getByName("implementation") {
        extendsFrom(library.get())
    }
    getByName("runtimeClasspath").extendsFrom(lor.get())
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components.getByName("java"))
        }
    }
    repositories {
        maven(System.getenv("local_maven_url") ?: "file://${project.projectDir}/repo")
    }
}

idea {
    module {
        excludeDirs.addAll(setOf(project.file("run"), project.file("runs"), project.file("run_server"), project.file("run_client"), project.file("run_game_test")))
    }
}