package com.blamejared;

import com.blamejared.gradle.mod.utils.GMUtils;
import com.blamejared.gradle.mod.utils.values.GitChangelogValue;
import com.diluv.schoomp.Webhook;
import com.diluv.schoomp.message.Message;
import com.diluv.schoomp.message.embed.Embed;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePluginExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        project.setVersion(GMUtils.updatingVersion(Util.property(project, "mod_version")));
        project.getTasks().register("postDiscord").configure(task -> {
            task.dependsOn(":fabric:publishCurseForge", ":neoforge:publishCurseForge");
            task.doLast(last -> {
                try {
                    final String name = Objects.requireNonNull(project.property("name"), "Cannot find 'name' property")
                            .toString();
                    final String avatar = Objects.requireNonNull(project.property("avatar"), "Cannot find 'avatar' property")
                            .toString();
                    final String minecraft = Objects.requireNonNull(project.property("minecraft"), "Cannot find 'minecraft' property")
                            .toString();
                    final String gitRepo = Objects.requireNonNull(project.property("git_repo"), "Cannot find 'git_repo' property")
                            .toString();
                    final Webhook webhook = new Webhook(GMUtils.locateProperty(project, "discordCFWebhook"), name + " CurseForge Gradle Upload");
                    
                    final Message message = new Message()
                            .setUsername(name)
                            .setAvatarUrl(avatar)
                            .setContent(name + " " + project.getVersion() + " for Minecraft " + minecraft + " has been published!");
                    
                    final Embed embed = new Embed();
                    final StringJoiner downloadSources = new StringJoiner("\n");
                    
                    Map.of("fabric", "<:fabric:932163720568782878>", "neoforge", "<:neoforged:1184738260371644446>")
                            .entrySet()
                            .stream()
                            .map(entry -> {
                                if(project.project(":" + entry.getKey())
                                        .getExtensions()
                                        .findByName("curse_file_url") instanceof String str) {
                                    return entry.getValue() + " [" + Util.capitalize(entry.getKey()) + "](" + str + ")";
                                }
                                
                                return null;
                            }).filter(Objects::nonNull)
                            .forEach(downloadSources::add);
                    
                    Stream.of("common", "fabric", "neoforge")
                            .map(s -> project.project(":" + s))
                            .map(subProject -> "<:maven:932165250738970634> `\"" + subProject.getGroup() + ":" + subProject.getExtensions()
                                    .getByType(BasePluginExtension.class)
                                    .getArchivesName().get() + ":" + subProject.getVersion() + "\"`")
                            .forEach(downloadSources::add);
                    
                    if(!downloadSources.toString().isEmpty()) {
                        embed.addField("Download", downloadSources.toString(), false);
                    }
                    final String changelog = project.getProviders()
                            .of(GitChangelogValue.class, parametersValueSourceSpec -> {
                                parametersValueSourceSpec.getParameters().getRepository().set(gitRepo);
                            }).get();
                    embed.addField("Changelog", changelog.substring(0, Math.min(changelog.length(), 1000)), false);
                    embed.setColor(0xF16436);
                    message.addEmbed(embed);
                    
                    webhook.sendMessage(message);
                } catch(IOException e) {
                    project.getLogger().error("Failed to push CF Discord webhook.");
                    try {
                        Files.writeString(project.file("post_discord_error.log")
                                .toPath(), Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .collect(Collectors.joining("\n")));
                    } catch(IOException ignored) {
                    }
                }
            });
        });
    }
    
}