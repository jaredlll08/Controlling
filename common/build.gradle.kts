import com.blamejared.controlling.gradle.Versions
import com.blamejared.controlling.gradle.Properties
plugins {
    id("java")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    id("com.blamejared.controlling.default")
}

minecraft {
    version(Versions.MINECRAFT)
    accessWideners(project.file("src/main/resources/${Properties.MODID}.accesswidener"))
    runs {
        client("Common Client") {
            workingDirectory(project.file("run"))
        }
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    implementation("com.blamejared.searchables:Searchables-common-${Versions.MINECRAFT}:${Versions.SEARCHABLES}")
}