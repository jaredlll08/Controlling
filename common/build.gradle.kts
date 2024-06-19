import com.blamejared.Versions
import com.blamejared.Properties

plugins {
    id("blamejared-java-conventions")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}
minecraft {
    version(Versions.MINECRAFT)
    accessWideners(project.file("src/main/resources/${Properties.MODID}.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    implementation("com.blamejared.searchables:Searchables-common-${Versions.MINECRAFT}:${Versions.SEARCHABLES}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}