import com.blamejared.gradle.mod.utils.GMUtils
import com.blamejared.controlling.gradle.Properties
import com.blamejared.controlling.gradle.Versions
import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("com.blamejared.controlling.default")
    id("com.blamejared.controlling.loader")
    id("net.neoforged.gradle.userdev") version ("7.0.71")
    id("com.modrinth.minotaur")
}

minecraft.accessTransformers.file(project.file("src/main/resources/META-INF/accesstransformer.cfg"))

runs {
    configureEach {
        modSource(project.sourceSets.main.get())
    }
    register("client") {
    }
}

dependencies {
    implementation("net.neoforged:neoforge:${Versions.NEO_FORGE}")
    compileOnly(project(":common"))
    implementation("com.blamejared.searchables:Searchables-neoforge-${Versions.MINECRAFT}:${Versions.SEARCHABLES}")
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)
    apiToken = GMUtils.locateProperty(project, "curseforgeApiToken") ?: 0

    val mainFile = upload(Properties.CURSE_PROJECT_ID, tasks.jar.get().archiveFile)
    mainFile.changelogType = "markdown"
    mainFile.changelog = GMUtils.smallChangelog(project, Properties.GIT_REPO)
    mainFile.releaseType = Constants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java ${Versions.JAVA}")
    mainFile.addGameVersion(Versions.MINECRAFT)
    mainFile.addModLoader("NeoForge")
    mainFile.addRequirement("searchables")

    doLast {
        project.ext.set("curse_file_url", "${Properties.CURSE_HOMEPAGE}/files/${mainFile.curseFileId}")
    }
}

modrinth {
    token.set(GMUtils.locateProperty(project, "modrinth_token"))
    projectId.set(Properties.MODRINTH_PROJECT_ID)
    changelog.set(GMUtils.smallChangelog(project, Properties.GIT_REPO))
    versionName.set("NeoForge-${Versions.MINECRAFT}-$version")
    versionType.set("release")
    gameVersions.set(listOf(Versions.MINECRAFT))
    uploadFile.set(tasks.jar.get())
    dependencies {
        required.project("searchables")
    }
}
tasks.modrinth.get().dependsOn(tasks.jar)