import com.blamejared.Properties
import com.blamejared.Versions
import com.blamejared.gradle.mod.utils.GMUtils
import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("blamejared-modloader-conventions")
    id("net.neoforged.moddev") version ("0.1.93")
    id("com.modrinth.minotaur")
}

neoForge {
    version = Versions.NEO_FORGE
    accessTransformers.add(file("src/main/resources/META-INF/accesstransformer.cfg").absolutePath)
    runs {
        register("client") {
            client()
        }
    }

    mods {
        register(Properties.MODID) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
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
    loaders.add("neoforge")
}
tasks.modrinth.get().dependsOn(tasks.jar)